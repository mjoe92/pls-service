package de.vw.paso.repository.masterdata;

import java.util.Collection;

import de.vw.paso.masterdata.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {

    Product findOneByProductKey(String productKey);

    Collection<Product> findByProductKeyIn(Collection<String> productKeys);

    Collection<Product> findBySetVersionId(Long setVersionId);
}
