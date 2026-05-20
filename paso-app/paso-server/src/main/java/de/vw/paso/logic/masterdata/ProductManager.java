package de.vw.paso.logic.masterdata;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import de.vw.paso.mapper.SetVersionMapper;
import de.vw.paso.masterdata.domain.Product;
import de.vw.paso.partlist.domain.SetVersion;
import de.vw.paso.repository.masterdata.ProductRepository;
import de.vw.paso.repository.user.UserRepository;
import de.vw.paso.service.masterdata.product.ProductDTO;
import de.vw.paso.user.domain.User;
import de.vw.paso.util.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductManager {

    protected static final ArrayList<String> PRODUCTS_TO_EXCLUDE = new ArrayList<>(
            Arrays.asList("FGK", "WPR", "PKL", "PGK", "PJK", "SPR"));

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductManager(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Set<ProductDTO> getAllProductDTOs() {
        return productRepository.findAll().stream().map(product -> convertToProductDTO(product, false))
                .filter(product -> !PRODUCTS_TO_EXCLUDE.contains(product.getProductType())).collect(Collectors.toSet());
    }

    @Transactional
    public ProductDTO saveProduct(ProductDTO productDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        User user = userRepository.findById(userId).orElseThrow(UnauthorizedException::new);

        String productKey = productDTO.getProductKey();

        Product product = user.getUserGroups().stream().map(userGroup -> {
            if (!userGroup.isWriteAccess()) {
                return null;
            }

            return userGroup.getVehicleConfigs().stream().map(config -> config.getVehicleProject().getProduct())
                    .filter(element -> element.getProductKey().equals(productKey)).findAny().orElse(null);
        }).filter(Objects::nonNull).findAny().orElseThrow(
                () -> new UnauthorizedException("Only users with the right write access can edit a product"));

        SetVersion entity = SetVersionMapper.toEntity(productDTO.getSetVersionDTO());
        product.setSetVersion(entity);

        product.setProductKey(productDTO.getProductKey());
        product.setProductType(productDTO.getProductType());
        product.setSetVersionId(productDTO.getSetVersionId());
        product.setTimestampChange(new Timestamp(System.currentTimeMillis()));
        product.setUserChange(userId);

        Product saved = productRepository.save(product);
        return convertToProductDTO(saved, true);
    }

    private ProductDTO convertToProductDTO(Product product, boolean isEditable) {
        return new ProductDTO(product.getProductKey(), product.getProductType(), product.getSetVersionId(),
                SetVersionMapper.toDto(product.getSetVersion()), isEditable);
    }
}
