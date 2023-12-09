package com.hursa.hursaknives.service;

import com.hursa.hursaknives.model.dto.ContactDTO;
import com.hursa.hursaknives.model.entity.ContactEntity;
import com.hursa.hursaknives.repo.ContactRepository;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ContactService {
  private final ContactRepository contactRepository;
  private final ModelMapper modelMapper;

  public ContactService(ContactRepository contactRepository, ModelMapper modelMapper) {
    this.contactRepository = contactRepository;
    this.modelMapper = modelMapper;
  }

  public ContactDTO initContacts() {
    if (contactRepository.count() == 0) {
      ContactEntity contactEntity =
          new ContactEntity()
              .setEmail("contacts@hursa.com")
              .setFirstName("Hursa")
              .setLastName("Knives")
              .setAdditionalInfo("For any questions or support please contact us")
              .setImageUrl("/images/knives_icon.png");

      ContactEntity saved = contactRepository.save(contactEntity);
      return modelMapper.map(saved, ContactDTO.class);
    }
    return null;
  }

  public List<ContactDTO> getAllContacts() {
    return contactRepository.findAll().stream()
        .map(c -> modelMapper.map(c, ContactDTO.class))
        .toList();
  }

  public void addContact(ContactDTO contactDTO) {
    if (contactDTO == null) {
      throw new IllegalArgumentException("Contact cannot be null");
    }
    if (contactDTO.getFirstName() == null) {
      throw new IllegalArgumentException("First name cannot be null");
    }
    if (contactDTO.getLastName() == null) {
      throw new IllegalArgumentException("Last name cannot be null");
    }
    if (contactDTO.getEmail() == null) {
      throw new IllegalArgumentException("Email cannot be null");
    }
    contactRepository.save(modelMapper.map(contactDTO, ContactEntity.class));
  }

  public void deleteContact(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("Id cannot be null");
    }
    contactRepository.deleteById(id);
  }

  public void updateContact(Long id, ContactDTO contactDTO) {
    if (id == null) {
      throw new IllegalArgumentException("Id cannot be null");
    }
    if (contactDTO == null) {
      throw new IllegalArgumentException("Contact cannot be null");
    }
    ContactEntity contactFound =
        this.contactRepository
            .findById(contactDTO.getId())
            .orElseThrow(() -> new IllegalArgumentException("Contact not found"));
    boolean isChanged = false;

    if (contactDTO.getFirstName() != null
        && !contactDTO.getFirstName().equals(contactFound.getFirstName())) {
      contactFound.setFirstName(contactDTO.getFirstName());
      isChanged = true;
    }
    if (contactDTO.getLastName() != null
        && !contactDTO.getLastName().equals(contactFound.getLastName())) {
      contactFound.setLastName(contactDTO.getLastName());
      isChanged = true;
    }
    if (contactDTO.getEmail() != null && !contactDTO.getEmail().equals(contactFound.getEmail())) {
      contactFound.setEmail(contactDTO.getEmail());
      isChanged = true;
    }
    if (contactDTO.getPhone() != null && !contactDTO.getPhone().equals(contactFound.getPhone())) {
      contactFound.setPhone(contactDTO.getPhone());
      isChanged = true;
    }
    if (contactDTO.getAdditionalInfo() != null
        && !contactDTO.getAdditionalInfo().equals(contactFound.getAdditionalInfo())) {
      contactFound.setAdditionalInfo(contactDTO.getAdditionalInfo());
      isChanged = true;
    }
    if (contactDTO.getImageUrl() != null
        && !contactDTO.getImageUrl().equals(contactFound.getImageUrl())) {
      contactFound.setImageUrl(contactDTO.getImageUrl());
      isChanged = true;
    }
    if (isChanged) {
      contactRepository.save(contactFound);
    }
  }

  public ContactDTO getContactById(Long id) {
    return contactRepository
        .findById(id)
        .map(c -> modelMapper.map(c, ContactDTO.class))
        .orElseThrow(() -> new IllegalArgumentException("Contact not found"));
  }

  public void removeContactImage(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("Id cannot be null");
    }
    ContactEntity contactFound =
        this.contactRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Contact not found"));
    contactFound.setImageUrl(null);
    contactRepository.save(contactFound);
  }
}
