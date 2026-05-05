package com.ecommerce.project.Controller;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ecommerce.project.Payload.AddressDTO;
import com.ecommerce.project.model.User;
import com.ecommerce.project.service.AddressService;
import com.ecommerce.project.util.AuthUtil;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class AddressController {

    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private AddressService addressService;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
        User user = authUtil.loggedInUser();
        logger.info("Creating address for current user");
        AddressDTO savedAddressDTO = addressService.createAddress(addressDTO, user);
        logger.info("Address created successfully for current user");
        return new ResponseEntity<>(savedAddressDTO, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAddresses() {
        User user = authUtil.loggedInUser();
        logger.debug("Fetching addresses for current user");
        List<AddressDTO> addressList = addressService.getUserAddresses(user);
        logger.debug("Fetched {} addresses for current user", addressList.size());
        return new ResponseEntity<List<AddressDTO>>(addressList, HttpStatus.OK);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId) {
        logger.debug("Fetching address by addressId={}", addressId);
        AddressDTO addressDTO = addressService.getAddressById(addressId);
        logger.debug("Fetched address by addressId={}", addressId);
        return new ResponseEntity<>(addressDTO, HttpStatus.OK);
    }

    @GetMapping("/user/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddresses() {
        User user = authUtil.loggedInUser();
        logger.debug("Fetching user addresses for current user");
        List<AddressDTO> addressList = addressService.getUserAddresses(user);
        logger.debug("Fetched {} user addresses for current user", addressList.size());
        return new ResponseEntity<List<AddressDTO>>(addressList, HttpStatus.OK);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long addressId, @RequestBody AddressDTO addressDTO) {
        logger.info("Updating address addressId={}", addressId);
        AddressDTO addressDTO1 = addressService.updateAddress(addressId, addressDTO);
        logger.info("Address updated successfully addressId={}", addressId);
        return new ResponseEntity<AddressDTO>(addressDTO1, HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
        logger.info("Deleting address addressId={}", addressId);
        String status = addressService.deleteAddress(addressId);
        logger.info("Address deleted successfully addressId={}", addressId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

}
