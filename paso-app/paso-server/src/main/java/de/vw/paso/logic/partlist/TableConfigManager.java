package de.vw.paso.logic.partlist;

import java.util.HashSet;
import java.util.List;

import de.vw.paso.logic.user.UserManager;
import de.vw.paso.repository.tableconfiguration.TableConfigRepository;
import de.vw.paso.repository.user.UserPropertyRepository;
import de.vw.paso.service.tableconfig.TableConfigDTO;
import de.vw.paso.tableconfig.TableConfig;
import de.vw.paso.user.PropertyType;
import de.vw.paso.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TableConfigManager {

    private static final List<String> requiredColumnIds = List.of("partNumber");

    private final TableConfigRepository tableConfigRepository;
    private final UserManager userManager;
    private final UserPropertyRepository userPropertyRepository;

    public TableConfigManager(TableConfigRepository tableConfigRepository, UserManager userManager,
            UserPropertyRepository userPropertyRepository) {
        this.tableConfigRepository = tableConfigRepository;
        this.userManager = userManager;
        this.userPropertyRepository = userPropertyRepository;
    }

    public TableConfigDTO saveConfig(TableConfigDTO configDTO) {
        return saveConfig(convertToEntity(configDTO), configDTO.isDefault());
    }

    private TableConfigDTO saveConfig(TableConfig config, boolean isDefault) {
        Long id = config.getId();
        boolean isPublic = config.isPublic();
        String name = config.getName();
        List<String> selectedColumns = config.getSelectedColumns();
        List<String> selectedColumnIds = config.getSelectedColumnIds();

        if (id != null) {
            config = tableConfigRepository.findById(id).orElseThrow();

            if (!config.getUserId().equals(userManager.getCurrentUserId())) {
                throw new RuntimeException("Cannot change layout for another user");
            }

            config.setPublic(isPublic);
            config.setName(name);
            config.setSelectedColumns(selectedColumns);
            config.setSelectedColumnIds(selectedColumnIds);
        }

        User user = userManager.getCurrentUser();
        config.setUser(user);

        TableConfig tableConfig = tableConfigRepository.save(config);

        // public view cannot be set as default
        if (!isPublic) {
            if (isDefault) {
                userManager.changeUserDefaultTableConfig(config);
            } else if (userManager.isConfigDefault(tableConfig.getId())) {
                userManager.changeUserDefaultTableConfig(null);
            }
        }

        return convertToDTO(tableConfig);
    }

    public List<TableConfigDTO> getAllConfigsForCurrentUser() {
        return tableConfigRepository.findByUserOrIsPublicTrue(userManager.getCurrentUser()).stream()
                .map(this::convertToDTO).toList();
    }

    @Transactional
    public void deleteTableConfig(Long id) {
        TableConfig tableConfig = tableConfigRepository.findById(id).orElseThrow();
        if (!tableConfig.isPublic()) {
            if (!tableConfig.getUserId().equals(userManager.getCurrentUserId())) {
                throw new RuntimeException("Cannot change layout for another user");
            } else if (!userPropertyRepository.findByTypeAndUserData(PropertyType.DEFAULT_TABLE_CONFIG, id.toString())
                    .isEmpty()) {
                throw new RuntimeException("This configuration is your or some else's default, thus cannot be deleted");
            }
        }

        tableConfigRepository.delete(tableConfig);
    }

    public TableConfigDTO convertToDTO(TableConfig tableConfig) {
        return new TableConfigDTO(tableConfig.getId(), tableConfig.getUserId(), tableConfig.getName(),
                tableConfig.getSelectedColumns(), tableConfig.getSelectedColumnIds(), tableConfig.isPublic(),
                userManager.isConfigDefault(tableConfig.getId()));
    }

    public TableConfig convertToEntity(TableConfigDTO tableConfigDTO) {
        if (!new HashSet<>(tableConfigDTO.getSelectedColumnIds()).containsAll(requiredColumnIds)) {
            throw new RuntimeException("Fields " + String.join(", ", requiredColumnIds) + "  has to be selected");
        }

        return new TableConfig(tableConfigDTO.getId(), userManager.getUser(tableConfigDTO.getUserId()),
                tableConfigDTO.getName(), tableConfigDTO.getSelectedColumns(), tableConfigDTO.getSelectedColumnIds(),
                tableConfigDTO.isPublic());
    }
}
