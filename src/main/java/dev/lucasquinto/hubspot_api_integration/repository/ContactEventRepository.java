package dev.lucasquinto.hubspot_api_integration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.lucasquinto.hubspot_api_integration.model.crm.contact.ContactEvent;

@Repository
public interface ContactEventRepository extends JpaRepository<ContactEvent, Long> {}
