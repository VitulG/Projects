package com.swag.app.repository;

import com.swag.app.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    @Query("SELECT c FROM Contact c JOIN c.person p WHERE p.name = :user")
    List<Contact> findAllBy(@Param("user") String user);
    Contact save(Contact contact);
}
