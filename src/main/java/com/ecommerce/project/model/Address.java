package com.ecommerce.project.model;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size( min = 5, message = "Street name must be ateast 5 character")
    private String street;

    @NotBlank
    @Size( min = 5, message = "Buidling name must be ateast 5 character")
    private String building;

    @NotBlank
    @Size( min = 3, message = "City name must be ateast 3 character")
    private String city;

    @NotBlank
    @Size( min = 3, message = "State name must be ateast 3 character")
    private String state;

    @NotBlank
    @Size( min = 3, message = "Country name must be ateast 3 character")
    private String country;

    @NotBlank
    @Size( min = 5, message = "Pincode name must be ateast 5 character")
    private String pinCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Creates address without users association.
     */
    public Address(String street, String building, String city, String state, String country, String pinCode) {
        this.street = street;
        this.building = building;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pinCode = pinCode;
    }
    
}
