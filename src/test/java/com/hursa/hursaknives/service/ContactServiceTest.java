package com.hursa.hursaknives.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hursa.hursaknives.model.dto.ContactDTO;
import com.hursa.hursaknives.model.entity.ContactEntity;
import com.hursa.hursaknives.repo.ContactRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.record.RecordModule;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {
  private final ModelMapper modelMapper = new ModelMapper().registerModule(new RecordModule());
  @Mock ContactRepository contactRepository;
  private ContactService serviceToTest;

  @BeforeEach
  void setup() {
    serviceToTest = new ContactService(contactRepository, modelMapper);
  }

  @Test
  void initContacts() {
    when(contactRepository.count()).thenReturn(0L);
    when(contactRepository.save(any())).thenReturn(new ContactEntity());
    assertDoesNotThrow(
        () -> serviceToTest.initContacts(), "initContacts should not throw an exception");
    verify(contactRepository).save(any());
    assertNotNull(serviceToTest.initContacts(), "Should not return null");
  }

  @Test
  void initContactsAlreadyExists() {
    when(contactRepository.count()).thenReturn(1L);
    assertNull(serviceToTest.initContacts(), "Should return null");
  }

  @Test
  void getAllContacts() {
    when(contactRepository.findAll()).thenReturn(List.of(new ContactEntity()));
    assertDoesNotThrow(
        () -> serviceToTest.getAllContacts(), "getAllContacts should not throw an exception");
    verify(contactRepository).findAll();
    assertNotNull(serviceToTest.getAllContacts(), "Should not return null");
    serviceToTest.getAllContacts().forEach(c -> assertNotNull(c, "Should not return null"));
  }

  @Test
  void addContact() {
    ContactDTO contactDTO =
        new ContactDTO()
            .setFirstName("test")
            .setLastName("test")
            .setEmail("test@test.com")
            .setPhone("+359888123456789")
            .setAdditionalInfo("test")
            .setImageUrl("test");
    when(contactRepository.save(any()))
        .thenReturn(modelMapper.map(contactDTO, ContactEntity.class));
    assertDoesNotThrow(
        () -> serviceToTest.addContact(contactDTO), "addContact should not throw an exception");
    verify(contactRepository).save(any());
  }

  @Test
  void addContactThrowsForNull() {
    IllegalArgumentException addContactShouldThrowAnException =
        assertThrows(
            IllegalArgumentException.class,
            () -> serviceToTest.addContact(null),
            "addContact should throw an exception");

    assertEquals(
        "Contact cannot be null",
        addContactShouldThrowAnException.getMessage(),
        "Incorrect exception message");
  }

  @Test
  void getContactById() {
    when(contactRepository.findById(1L)).thenReturn(Optional.of(new ContactEntity()));
    assertDoesNotThrow(
        () -> serviceToTest.getContactById(1L), "getContactById should not throw an exception");
    verify(contactRepository).findById(1L);
    assertNotNull(serviceToTest.getContactById(1L), "Should not return null");
  }

  @Test
  void addContactShouldThrowForNullFirstName() {
    ContactDTO contactDTO =
        new ContactDTO()
            .setLastName("test")
            .setEmail("test@test.com")
            .setPhone("+359888123456789")
            .setAdditionalInfo("test")
            .setImageUrl("test");
    IllegalArgumentException addContactShouldThrowAnException =
        assertThrows(
            IllegalArgumentException.class,
            () -> serviceToTest.addContact(contactDTO),
            "addContact should throw an exception");

    assertEquals(
        "First name cannot be null",
        addContactShouldThrowAnException.getMessage(),
        "Incorrect exception message");
  }

  @Test
  void addContactShouldThrowForNullLastName() {
    ContactDTO contactDTO =
        new ContactDTO()
            .setFirstName("test")
            .setEmail("test@test.com")
            .setPhone("+359888123456789")
            .setAdditionalInfo("test")
            .setImageUrl("test");
    IllegalArgumentException addContactShouldThrowAnException =
        assertThrows(
            IllegalArgumentException.class,
            () -> serviceToTest.addContact(contactDTO),
            "addContact should throw an exception");

    assertEquals(
        "Last name cannot be null",
        addContactShouldThrowAnException.getMessage(),
        "Incorrect exception message");
  }

  @Test
  void addContactShouldThrowForNullEmail() {
    ContactDTO contactDTO =
        new ContactDTO()
            .setFirstName("test")
            .setLastName("test")
            .setPhone("+359888123456789")
            .setAdditionalInfo("test")
            .setImageUrl("test");
    IllegalArgumentException addContactShouldThrowAnException =
        assertThrows(
            IllegalArgumentException.class,
            () -> serviceToTest.addContact(contactDTO),
            "addContact should throw an exception");

    assertEquals(
        "Email cannot be null",
        addContactShouldThrowAnException.getMessage(),
        "Incorrect exception message");
  }

  @Test
  void updateContact() {
    ContactDTO contactDTO =
        new ContactDTO()
            .setId(1L)
            .setFirstName("test")
            .setLastName("test")
            .setEmail("test@test.com")
            .setPhone("+359888123456789")
            .setAdditionalInfo("test")
            .setImageUrl("test");
    when(contactRepository.save(any()))
        .thenReturn(modelMapper.map(contactDTO, ContactEntity.class));
    when(contactRepository.findById(1L)).thenReturn(Optional.of(new ContactEntity()));
    assertDoesNotThrow(
        () -> serviceToTest.updateContact(1L, contactDTO),
        "updateContact should not throw an exception");
    verify(contactRepository).save(any());
  }

  @Test
  void updateContactThrowsForNull() {
    IllegalArgumentException updateContactShouldThrowAnException =
        assertThrows(
            IllegalArgumentException.class,
            () -> serviceToTest.updateContact(null, null),
            "updateContact should throw an exception");

    assertEquals(
        "Id cannot be null",
        updateContactShouldThrowAnException.getMessage(),
        "Incorrect exception message");

    IllegalArgumentException updateContactShouldThrowAnExceptionForDTO =
        assertThrows(
            IllegalArgumentException.class,
            () -> serviceToTest.updateContact(1L, null),
            "updateContact should throw an exception");
    assertEquals(
        "Contact cannot be null",
        updateContactShouldThrowAnExceptionForDTO.getMessage(),
        "Incorrect exception message");
  }

  @Test
  void deleteContactThrowsForNull() {
    IllegalArgumentException deleteContactShouldThrowAnException =
        assertThrows(
            IllegalArgumentException.class,
            () -> serviceToTest.deleteContact(null),
            "deleteContact should throw an exception");

    assertEquals(
        "Id cannot be null",
        deleteContactShouldThrowAnException.getMessage(),
        "Incorrect exception message");
  }

  @Test
  void getContactByIdThrowsForNotExistingContact() {
    IllegalArgumentException getContactByIdShouldThrowAnException =
        assertThrows(
            IllegalArgumentException.class,
            () -> serviceToTest.getContactById(Long.MAX_VALUE),
            "getContactById should throw an exception");

    assertEquals(
        "Contact not found",
        getContactByIdShouldThrowAnException.getMessage(),
        "Incorrect exception message");
  }

  @Test
  void removeContactImage() {
    when(contactRepository.findById(1L)).thenReturn(Optional.of(new ContactEntity()));
    assertDoesNotThrow(
        () -> serviceToTest.removeContactImage(1L),
        "removeContactImage should not throw an exception");
    verify(contactRepository).save(any());
  }

  @Test
  void removeContactImageThrowsForNull() {
    IllegalArgumentException removeContactImageShouldThrowAnException =
        assertThrows(
            IllegalArgumentException.class,
            () -> serviceToTest.removeContactImage(null),
            "removeContactImage should throw an exception");

    assertEquals(
        "Id cannot be null",
        removeContactImageShouldThrowAnException.getMessage(),
        "Incorrect exception message");
  }
}
