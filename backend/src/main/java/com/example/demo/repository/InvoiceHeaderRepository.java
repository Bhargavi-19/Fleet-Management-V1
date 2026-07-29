package com.example.demo.repository;

import java.util.Optional;

<<<<<<< HEAD
import org.springframework.data.jpa.repository.JpaRepository;
=======
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
>>>>>>> Developer
import org.springframework.stereotype.Repository;

import com.example.demo.entity.InvoiceHeader;

@Repository
public interface InvoiceHeaderRepository extends JpaRepository<InvoiceHeader, Long> {

    Optional<InvoiceHeader> findByBookingId(Long bookingId);

<<<<<<< HEAD
=======
    /**
     * Invoices raised at one hub, newest first, with an optional search term.
     *
     * The search matches invoice number, booking id, customer name, e-mail or
     * vehicle registration. Passing null or an empty string returns everything.
     */
    @Query("""
            SELECT i FROM InvoiceHeader i
            WHERE (i.pickupHubId = :hubId OR i.dropoffHubId = :hubId)
              AND ( :search IS NULL OR :search = ''
                    OR LOWER(i.invoiceNo)      LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(i.firstName)      LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(i.lastName)       LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(i.email)          LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(i.registrationNo) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR CAST(i.bookingId AS string) LIKE CONCAT('%', :search, '%') )
            """)
    Page<InvoiceHeader> searchByHub(
            @Param("hubId") Integer hubId,
            @Param("search") String search,
            Pageable pageable);

    long countByPickupHubId(Integer hubId);

>>>>>>> Developer
}