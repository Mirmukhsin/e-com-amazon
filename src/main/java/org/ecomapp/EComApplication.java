package org.ecomapp;

import org.ecomapp.cartMS.models.Cart;
import org.ecomapp.cartMS.models.CartItem;
import org.ecomapp.cartMS.repository.CartItemRepository;
import org.ecomapp.cartMS.repository.CartRepository;
import org.ecomapp.productMS.enums.ProductStatus;
import org.ecomapp.productMS.models.Category;
import org.ecomapp.productMS.models.Product;
import org.ecomapp.productMS.models.ProductVariant;
import org.ecomapp.productMS.repositories.CategoryRepository;
import org.ecomapp.productMS.repositories.ProductRepository;
import org.ecomapp.productMS.repositories.ProductVariantRepository;
import org.ecomapp.userMS.enums.Label;
import org.ecomapp.userMS.enums.UserStatus;
import org.ecomapp.userMS.models.Address;
import org.ecomapp.userMS.models.Role;
import org.ecomapp.userMS.models.SellerProfile;
import org.ecomapp.userMS.models.User;
import org.ecomapp.userMS.repositories.AddressRepository;
import org.ecomapp.userMS.repositories.RoleRepository;
import org.ecomapp.userMS.repositories.SellerRepository;
import org.ecomapp.userMS.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;

@SpringBootApplication(scanBasePackages = "org.ecomapp")
public class EComApplication {

    public static void main(String[] args) {
        SpringApplication.run(EComApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(PasswordEncoder passwordEncoder, UserRepository userRepository,
                             CategoryRepository categoryRepository, SellerRepository sellerRepository, ProductRepository productRepository,
                             ProductVariantRepository productVariantRepository, AddressRepository addressRepository,
                             CartRepository cartRepository, CartItemRepository cartItemRepository, RoleRepository roleRepository) {
        return args -> {
            Role buyerRole = Role.builder().name("BUYER").build();
            Role sellerRole = Role.builder().name("SELLER").build();
            Role adminRole = Role.builder().name("ADMIN").build();
            roleRepository.saveAll(List.of(buyerRole, sellerRole, adminRole));

            User buyer = User.builder()
                    .email("buyer@gmail.com")
                    .password(passwordEncoder.encode("buyer111"))
                    .status(UserStatus.ACTIVE)
                    .roles(List.of(buyerRole))
                    .fullName("Buyer 1")
                    .phone("+998915129737")
                    .build();

            User seller = User.builder()
                    .email("seller@gmail.com")
                    .password(passwordEncoder.encode("seller111"))
                    .status(UserStatus.ACTIVE)
                    .roles(List.of(buyerRole, sellerRole))
                    .fullName("Seller 2")
                    .phone("+998997501208")
                    .build();

            User admin = User.builder()
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("admin111"))
                    .status(UserStatus.ACTIVE)
                    .roles(List.of(adminRole))
                    .fullName("Admin 0")
                    .phone("+998991221206")
                    .build();

            userRepository.saveAll(List.of(buyer, seller, admin));

            Address address = Address.builder()
                    .label(Label.HOME)
                    .street("2-mavze")
                    .city("Toshkent")
                    .state("14a")
                    .zip("19311")
                    .country("Uzbekistan")
                    .user(buyer)
                    .build();

            addressRepository.save(address);

            SellerProfile sellerProfile = SellerProfile.builder()
                    .storeName("ROG & Asus")
                    .storeDescription("Republic of Gamers")
                    .user(seller)
                    .build();

            sellerRepository.save(sellerProfile);

            Category parent = Category.builder().name("Computer").build();
            Category laptop = Category.builder().name("Laptop").parent(parent).build();
            Category pc = Category.builder().name("PC").parent(parent).build();

            categoryRepository.saveAll(List.of(parent, laptop, pc));

            Product productLaptop = Product.builder()
                    .name("ROG STRIX G17 2022")
                    .description("Gaming Laptop")
                    .basePrice(1200.0)
                    .category(laptop)
                    .seller(seller)
                    .status(ProductStatus.ACTIVE)
                    .build();

            Product productPC = Product.builder()
                    .name("Samsung")
                    .description("Gaming PC")
                    .basePrice(1350.0)
                    .category(pc)
                    .seller(seller)
                    .status(ProductStatus.ACTIVE)
                    .build();

            productRepository.saveAll(List.of(productLaptop, productPC));

            ProductVariant laptopVariant = ProductVariant.builder()
                    .sku("G713 RC")
                    .price(1250.0)
                    .stockQuantity(10)
                    .attributes(Map.of(
                            "memory", "500GB SSD",
                            "Video card", "Geforce RTX 3050 4GB",
                            "CPU", "AMD Ryzen 7 6800H"))
                    .product(productLaptop)
                    .build();

            ProductVariant pcVariant = ProductVariant.builder()
                    .sku("Samsung")
                    .price(1350.0)
                    .stockQuantity(5)
                    .attributes(Map.of(
                            "memory", "1TB SSD",
                            "Video card", "Geforce RTX 4050 6GB",
                            "CPU", "Intel core i7 13200H"))
                    .product(productPC)
                    .build();

            productVariantRepository.saveAll(List.of(laptopVariant, pcVariant));

            Cart cart = Cart.builder()
                    .user(buyer)
                    .build();

            cartRepository.save(cart);

            CartItem cartItem = CartItem.builder()
                    .quantity(2)
                    .cart(cart)
                    .productVariant(laptopVariant)
                    .build();

            cartItemRepository.save(cartItem);
        };
    }
}
