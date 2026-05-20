package de.vw.paso.service.masterdata.product;

public interface ProductRestService {
  String URL = "/api/products";

  ProductDTOSet getProducts();

  ProductDTO updateProduct(ProductDTO productDTO);
}
