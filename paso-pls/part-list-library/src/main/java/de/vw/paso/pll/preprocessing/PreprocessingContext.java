package de.vw.paso.pll.preprocessing;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class PreprocessingContext {

	private final List<File> inputFiles;
	private Map<TiWhFileType, File> mappedFiles = new HashMap<>();

	private String product;

  private AtomicInteger ruleIdGenerator = new AtomicInteger();
  private Map<String, Integer> ruleIdByRuleMap = new HashMap<>();

  private Set<String> skippedNodes = new HashSet<>();

  private boolean replaceIds = false;

  private AtomicInteger ppfIdGenerator = new AtomicInteger();
  private Map<String, String> ppfIdByGuidMap = new HashMap<>();

  public PreprocessingContext(List<File> files) {
    inputFiles = files;
  }

  public List<File> getInputFiles() {
    return inputFiles;
  }

  public Map<TiWhFileType, File> getMappedFiles() {
    return mappedFiles;
  }

  public Integer addRule(String rule) {
    return ruleIdByRuleMap.computeIfAbsent(rule, unknownRule -> ruleIdGenerator.getAndIncrement());
  }

  public Integer getRuleId(String rule) {
    return ruleIdByRuleMap.get(rule);
  }

  public Set<Map.Entry<String, Integer>> getRules() {
	  return ruleIdByRuleMap.entrySet();
  }

  public void addSkippedNode(String nodeId) {
	  skippedNodes.add(nodeId);
  }

  public boolean isSkippedNode(String nodeId) {
	  return skippedNodes.contains(nodeId);
  }

  public String getProduct() {
    return product;
  }

  public void setProduct(String product) {
    this.product = product;
  }

  public boolean isIdsReplaced() {
    return replaceIds;
  }

  public void setReplaceIds(boolean replaceIds) {
    this.replaceIds = replaceIds;
  }

  public String mapId(String guid) {
    return ppfIdByGuidMap.computeIfAbsent(guid, unknown -> String.valueOf(ppfIdGenerator.getAndIncrement()));
  }
}
