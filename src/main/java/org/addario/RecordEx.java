package org.addario;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public record RecordEx(String reference_id,
                       String first_name,
                       String middle_name,
                       String last_name,
                       String address,
                       String city,
                       String state,
                       String postal_code,
                       String country,
                       String phone,
                       String dob,
                       String nationality,
                       String email,
                       String account_id,
                       String reference_info,
                       String tax_id,
                       String passport,
                       String national_id) {
    static Random r = new Random();
    static List<String> names = List.of("Alice", "Bob", "Claire", "David", "Emma", "Frank", "Grace", "Henry",
            "Isaac", "Julia", "Kevin", "Laura", "Michael", "Natalie", "Oliver", "Paul", "Quinn", "Rachel", "Sarah", "Thomas",
            "Ursula", "Victor", "William", "Xavier", "Yvonne", "Zoe");

    public RecordEx() {
        this(UUID.randomUUID().toString(),
                names.get(r.nextInt(names.size())),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );
    }
}
