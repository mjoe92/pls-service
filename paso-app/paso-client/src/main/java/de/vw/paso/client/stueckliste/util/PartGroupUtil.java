package de.vw.paso.client.stueckliste.util;

import java.util.List;

import de.vw.paso.client.valueobject.PartGroupVMO;
import de.vw.paso.service.masterdata.partgroup.PartGroupDTO;

public class PartGroupUtil {

    private static final String SEPARATOR = "-";

    public static void sortByParent(List<PartGroupVMO> partGroupsList) {
        partGroupsList.sort((t1, t2) -> {
            if (isCategory(t1)) {
                if (isCategory(t2)) {
                    return t1.getCategory() - t2.getCategory();
                } else {
                    return -1;
                }
            } else if (isMgr(t1)) {
                if (isCategory(t2)) {
                    return 1;
                } else if (isMgr(t2)) {
                    return t1.getMgr() - t2.getMgr();
                } else {
                    return -1;
                }
            } else {
                if (isCategory(t2) || isMgr(t2)) {
                    return 1;
                } else {
                    return t1.getUgr() - t2.getUgr();
                }
            }
        });
    }

    public static boolean isCategory(PartGroupVMO partGroupVMO) {
        return partGroupVMO.getMgr() == null;
    }

    public static boolean isMgr(PartGroupVMO partGroupVMO) {
        return partGroupVMO.getUgr() == null;
    }

    public static String groupToString(Integer integer) {
        if (integer == null) {
            return "";
        }
        return String.format("%03d", integer);
    }

    public static boolean isUgrNumeric(String strNum) {
        try {
            double d = Integer.parseInt(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    public static String getParentKeyForPartGroup(PartGroupDTO partGroup) {
        if (partGroup == null || partGroup.isCategory()) {
            return null;
        }

        if (partGroup.isUgr()) {
            if (partGroup.getCategory() == null) {
                return PartGroupUtil.groupToString(partGroup.getMgr());
            } else {
                return partGroup.getCategory().toString() + SEPARATOR + PartGroupUtil.groupToString(partGroup.getMgr());
            }
        }

        if (partGroup.getCategory() != null) {
            return partGroup.getCategory().toString();
        }

        return null;
    }

    public static String getKeyForPartGroup(PartGroupDTO partGroup) {
        String key = null;

        if (partGroup.isCategory()) {
            if (partGroup.getCategory() != null) {
                key = partGroup.getCategory().toString();
            }
        } else if (partGroup.isMgr()) {
            if (partGroup.getCategory() == null) {
                key = PartGroupUtil.groupToString(partGroup.getMgr());
            } else {
                key = partGroup.getCategory().toString() + SEPARATOR + PartGroupUtil.groupToString(partGroup.getMgr());
            }
        } else if (partGroup.isUgr()) {
            if (partGroup.getCategory() == null) {
                key = PartGroupUtil.groupToString(partGroup.getMgr()) + SEPARATOR + PartGroupUtil.groupToString(
                        partGroup.getUgr());
            } else {
                key = partGroup.getCategory().toString() + SEPARATOR + PartGroupUtil.groupToString(partGroup.getMgr())
                        + SEPARATOR + PartGroupUtil.groupToString(partGroup.getUgr());
            }
        }

        return key;
    }

    public static String getKeyForPartGroupWithMgrEnd(PartGroupDTO partGroup, int mgr) {
        String key = null;

        if (partGroup.isMgr()) {
            if (partGroup.getCategory() == null) {
                key = PartGroupUtil.groupToString(mgr);
            } else {
                key = partGroup.getCategory().toString() + SEPARATOR + PartGroupUtil.groupToString(mgr);
            }
        } else if (partGroup.isUgr()) {
            if (partGroup.getCategory() == null) {
                key = PartGroupUtil.groupToString(mgr) + SEPARATOR + PartGroupUtil.groupToString(partGroup.getUgr());
            } else {
                key = partGroup.getCategory().toString() + SEPARATOR + PartGroupUtil.groupToString(mgr) + SEPARATOR
                        + PartGroupUtil.groupToString(partGroup.getUgr());
            }
        }

        return key;
    }

}
