package de.vw.paso.service.product;

import de.vw.paso.logic.masterdata.ProductManager;
import de.vw.paso.service.masterdata.product.ProductDTO;
import de.vw.paso.service.masterdata.product.ProductDTOSet;
import de.vw.paso.service.masterdata.product.ProductRestService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = ProductRestService.URL)
public class ProductRestController implements ProductRestService {

    private final ProductManager productManager;

    public ProductRestController(ProductManager productManager) {
        this.productManager = productManager;
    }

    @Override
    @GetMapping
    public ProductDTOSet getProducts() {
        return new ProductDTOSet(productManager.getAllProductDTOs());
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO updateProduct(@RequestBody ProductDTO productDTO) {
        return productManager.saveProduct(productDTO);
    }
}