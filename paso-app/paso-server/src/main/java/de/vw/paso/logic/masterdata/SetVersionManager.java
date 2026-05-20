package de.vw.paso.logic.masterdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import de.vw.paso.masterdata.domain.Product;
import de.vw.paso.partlist.domain.SetKey;
import de.vw.paso.partlist.domain.SetVersion;
import de.vw.paso.repository.masterdata.ProductRepository;
import de.vw.paso.repository.partlist.SetKeyRepository;
import de.vw.paso.repository.partlist.SetVersionRepository;
import de.vw.paso.repository.user.UserRepository;
import de.vw.paso.repository.vehicle.VehicleConfigRepository;
import de.vw.paso.user.domain.User;
import de.vw.paso.util.DataNotFoundException;
import de.vw.paso.util.SetVersionReferenceException;
import de.vw.paso.util.UnauthorizedException;
import de.vw.paso.util.UnmodifiableEntryException;
import de.vw.paso.utility.StringConstant;
import de.vw.paso.vehicle.domain.VehicleConfig;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SetVersionManager {

    private static final String DEFAULT_SET_VERSION_NAME = "DEFAULT";

    private final SetVersionRepository setVersionRepository;
    private final SetKeyRepository setKeyRepository;
    private final VehicleConfigRepository vehicleConfigRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public SetVersionManager(SetVersionRepository setVersionRepository, SetKeyRepository setKeyRepository,
            VehicleConfigRepository vehicleConfigRepository, ProductRepository productRepository,
            UserRepository userRepository) {
        this.setVersionRepository = setVersionRepository;
        this.setKeyRepository = setKeyRepository;
        this.vehicleConfigRepository = vehicleConfigRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public Collection<SetVersion> loadSetVersions() {
        return setVersionRepository.findAll();
    }

    public SetVersion updateSetVersion(SetVersion newSetVersion) {
        SetVersion existingSetVersion = setVersionRepository.findById(newSetVersion.getId()).orElseThrow();
        existingSetVersion.setChange(newSetVersion.getUserChange());
        existingSetVersion.setName(newSetVersion.getName());

        return existingSetVersion;
    }

    public SetVersion addSetVersion(SetVersion setVersion, Long copyFromSetVersionId) {
        SetVersion savedSetVersion = saveSetVersion(setVersion);
        copySetKeysBySetVersionId(savedSetVersion.getId(), copyFromSetVersionId);

        return savedSetVersion;
    }

    @Transactional
    public void deleteSetVersion(Long id) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findById(userId).orElseThrow(UnauthorizedException::new);

        if (!user.isAdmin()) {
            throw new UnauthorizedException("Only admins can delete set versions");
        }

        SetVersion setVersion = setVersionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("This set version is already deleted"));

        if (setVersion.getName().equals(DEFAULT_SET_VERSION_NAME)) {
            throw new UnmodifiableEntryException("Default set version cannot be deleted");
        }

        Collection<Product> products;
        Collection<SetKey> setKeys;
        Collection<VehicleConfig> vehicleConfigs;

        try (ExecutorService executorService = Executors.newFixedThreadPool(4)) {
            Future<Collection<Product>> productListThread = executorService.submit(
                    () -> productRepository.findBySetVersionId(id));
            Future<Collection<SetKey>> setKeyListThread = executorService.submit(
                    () -> setKeyRepository.findByIdVersion(id));
            Future<List<VehicleConfig>> vehicleConfigListThread = executorService.submit(
                    () -> vehicleConfigRepository.findBySetVersionId(id));
            Future<SetVersion> defaultVehicleConfigThread = executorService.submit(
                    () -> setVersionRepository.findByName(DEFAULT_SET_VERSION_NAME).orElseThrow());

            products = productListThread.get();
            setKeys = setKeyListThread.get();
            vehicleConfigs = vehicleConfigListThread.get();
            var defaultVehicleConfig = defaultVehicleConfigThread.get();
            var softDeletedVehicleConfigs = vehicleConfigs.stream().filter(config -> config.getDeletionDate() != null)
                    .peek(config -> {
                        config.setSetVersion(defaultVehicleConfig);
                        config.setSetVersionId(defaultVehicleConfig.getId());
                    }).toList();

            vehicleConfigRepository.saveAll(softDeletedVehicleConfigs);

            vehicleConfigs = vehicleConfigs.stream().filter(config -> config.getDeletionDate() == null).toList();

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new SetVersionReferenceException(e.getMessage());
        }

        if (!products.isEmpty() || !vehicleConfigs.isEmpty()) {
            StringBuilder sb = new StringBuilder(
                    "This set version can not be deleted, because it references other items, namely: ");

            if (!products.isEmpty()) {
                String productNames = products.stream().map(Product::getProductKey)
                        .collect(Collectors.joining(StringConstant.COMMA_SPACE));

                sb.append("\nproduct names: ").append(productNames);
            }

            if (!vehicleConfigs.isEmpty()) {
                String vehicleNames = vehicleConfigs.stream()
                        .map(config -> StringConstant.LEFT_PARENTHESIS + config.toString()
                                + StringConstant.RIGHT_PARENTHESIS)
                        .collect(Collectors.joining(StringConstant.COMMA_SPACE));

                sb.append("\nvehicle configs: ").append(vehicleNames);
            }

            throw new SetVersionReferenceException(sb.toString());
        }

        setKeyRepository.deleteAll(setKeys);
        setVersionRepository.delete(setVersion);
    }

    private void copySetKeysBySetVersionId(Long setVersionId, Long copyFromSetVersionId) {
        if (copyFromSetVersionId == null) {
            return;
        }

        Collection<SetKey> allKeysByIdVersion = setKeyRepository.findAllByIdVersion(copyFromSetVersionId);
        Collection<SetKey> newlyCreatedSetKeys = new ArrayList<>(allKeysByIdVersion.size());
        for (SetKey setKey : allKeysByIdVersion) {
            SetKey newSetKey = new SetKey(setKey.getSetKey(), setKey.getDescription(), setKey.getParentSetKey(),
                    setVersionId);
            newlyCreatedSetKeys.add(newSetKey);
        }

        setKeyRepository.saveAll(newlyCreatedSetKeys);
    }

    private SetVersion saveSetVersion(SetVersion setVersion) {
        return setVersionRepository.save(setVersion);
    }
}
