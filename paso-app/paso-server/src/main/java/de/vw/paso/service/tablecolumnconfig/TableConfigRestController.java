package de.vw.paso.service.tablecolumnconfig;

import de.vw.paso.logic.partlist.TableConfigManager;
import de.vw.paso.service.tableconfig.TableConfigDTO;
import de.vw.paso.service.tableconfig.TableConfigListDTO;
import de.vw.paso.service.tableconfig.TableConfigRestService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(TableConfigRestService.URL)
public class TableConfigRestController implements TableConfigRestService {

    private final TableConfigManager tableConfigManager;

    public TableConfigRestController(TableConfigManager tableConfigManager) {
        this.tableConfigManager = tableConfigManager;
    }

    @Override
    @GetMapping
    @Transactional
    public TableConfigListDTO getConfigurationsForUser() {
        return new TableConfigListDTO(tableConfigManager.getAllConfigsForCurrentUser());
    }

    @Override
    @PostMapping
    @Transactional
    public TableConfigDTO saveConfiguration(@RequestBody TableConfigDTO configDTO) {
        return tableConfigManager.saveConfig(configDTO);
    }

    @Override
    @DeleteMapping("/{id}")
    public void deleteConfiguration(@PathVariable Long id) {
        tableConfigManager.deleteTableConfig(id);
    }
}
