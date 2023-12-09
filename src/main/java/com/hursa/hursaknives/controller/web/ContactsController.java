package com.hursa.hursaknives.controller.web;

import com.hursa.hursaknives.model.dto.ContactDTO;
import com.hursa.hursaknives.model.dto.ProfileBindingModel;
import com.hursa.hursaknives.service.ContactService;
import com.hursa.hursaknives.service.FileUploadService;
import com.hursa.hursaknives.service.UserService;
import jakarta.validation.Valid;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/contacts")
public class ContactsController {
  private final ContactService contactService;
  private final UserService userService;
  private final Logger logger = Logger.getLogger(ContactsController.class.getName());
  private final FileUploadService fileUploadService;

  public ContactsController(
      ContactService contactService, UserService userService, FileUploadService fileUploadService) {
    this.contactService = contactService;
    this.userService = userService;
    this.fileUploadService = fileUploadService;
  }

  @ModelAttribute("userProfile")
  public ProfileBindingModel getUserProfile() {
    return userService
        .getUserProfile(SecurityContextHolder.getContext().getAuthentication().getName())
        .orElse(null);
  }

  @ModelAttribute("contacts")
  public List<ContactDTO> getAllContacts() {
    return contactService.getAllContacts();
  }

  @GetMapping
  public String contacts() {
    return "contacts";
  }

  @GetMapping("/admin/add")
  public String addContact(Model model) {
    if (!model.containsAttribute("contactDTO")) {
      model.addAttribute("contactDTO", new ContactDTO());
    }
    return "contactAdd";
  }

  @PostMapping("/admin/add")
  public String addContact(
      @RequestParam MultipartFile image,
      @Valid ContactDTO contactDTO,
      BindingResult bindingResult,
      RedirectAttributes rAtt) {
    if (bindingResult.hasErrors()) {
      rAtt.addFlashAttribute("contactDTO", contactDTO);
      rAtt.addFlashAttribute(
          "org.springframework.validation.BindingResult.contactDTO", bindingResult);
      rAtt.addFlashAttribute("errorMessage", "Unable to add contact");
      return "redirect:/contacts/admin/add";
    }
    contactDTO.setImageUrl(uploadImage(image));
    contactService.addContact(contactDTO);
    return "redirect:/contacts";
  }

  @GetMapping("/admin/edit/{id}")
  public String editContact(@PathVariable Long id, Model model) {
    model.addAttribute("contactDTO", contactService.getContactById(id));
    return "contactEdit";
  }

  @DeleteMapping("/admin/edit/{id}")
  public String removeContactImage(@PathVariable Long id) {
    contactService.removeContactImage(id);
    return "redirect:/contacts/admin/edit/" + id;
  }

  @PostMapping("/admin/edit/{id}")
  public String editContact(
      @PathVariable Long id,
      @RequestParam MultipartFile image,
      @Valid ContactDTO contactDTO,
      BindingResult bindingResult,
      RedirectAttributes rAtt) {
    if (bindingResult.hasErrors()) {
      rAtt.addFlashAttribute("contactDTO", contactDTO);
      rAtt.addFlashAttribute(
          "org.springframework.validation.BindingResult.contactDTO", bindingResult);
      rAtt.addFlashAttribute("errorMessage", "Unable to update contact!");
      return "redirect:/contacts/admin/edit/" + id;
    }
    contactDTO.setImageUrl(uploadImage(image));
    contactService.updateContact(id, contactDTO);
    rAtt.addFlashAttribute("successMessage", "Successfully updated contact!");
    return "redirect:/contacts/admin/edit/" + id;
  }

  @DeleteMapping("/admin/delete/{id}")
  public String removeContact(@PathVariable Long id) {
    contactService.deleteContact(id);
    return "redirect:/contacts";
  }

  private String uploadImage(MultipartFile image) {
    if (!image.isEmpty()) {
      try (InputStream input = image.getInputStream()) {
        try {
          ImageIO.read(input).toString();
          // It's an images (only BMP, GIF, JPEG, PNG, TIFF and WBMP are recognized).
        } catch (Exception e) {
          return null;
        }
      } catch (Exception e) {
        return null;
      }
      try {
        return fileUploadService.uploadFile(image).get("url");
      } catch (Exception e) {
        logger.warning(e.getMessage());
      }
    }
    return null;
  }
}
