OPTIONS (SKIP=1)
LOAD DATA
APPEND
INTO TABLE TI_WH_EBOM WHEN TEILENUMMER != '                  '
(
  TI_WH_EBOM_ID             "SEQ_TI_WH_EBOM.NEXTVAL",
  TI_WH_IMPORT_ID           "3990",
  IMPORT_DATE               "CURRENT_TIMESTAMP",
  
  NODE_ID                   position(1:32)      CHAR(32),
  VWS                       position(33:43)     INTEGER EXTERNAL,
  SORT_                     position(53:58)     INTEGER EXTERNAL,
  PRNR_REGEL                position(59:138)    CHAR(80),
  ENTWICKLUNGSSTAND_DT      position(139:148)   DATE 'YYYY-MM-DD',
  SET_KZ                    position(149:151)   CHAR(3),
  GEWICHTSSTEUERKZ          position(152:152)   CHAR(1),
  PROD                      position(164:167)   CHAR(4) "TRIM(:PROD)",
  TEILENUMMER               position(168:185)   CHAR(18),
  TNR_VORNUMMER             position(168:170)   CHAR(3),
  TNR_MITTELGRUPPE          position(171:173)   CHAR(3),
  TNR_ENDNUMMER             position(174:176)   CHAR(3),
  TNR_INDEX                 position(177:178)   CHAR(2),
  NODE_VALUE                position(186:225)   CHAR(40),
  NODE_TYPE                 position(226:233)   CHAR(8),
  NODE_LABEL                position(234:293)   CHAR(60),
  PRODUKTSTRUKTUR           position(294:296)   CHAR(3),
  AGGREGAT                  position(297:300)   CHAR(4),
  ANZAHL                    position(445:452)   CHAR(8),
  MENGENEINHEIT             position(458:460)   CHAR(3) "TRIM(:MENGENEINHEIT)",
  MEE                       position(461:461)   CHAR(1),
  KOSTENGRUPPE              position(462:465)   CHAR(4),
  KONSTRUKTIONSGRUPPE       position(466:466)   CHAR(1),
  WAHLWEISE_FALL            position(467:468)   CHAR(2),
  WAHLWEISE_NR              position(469:470)   CHAR(2),
  EINSATZ_DATUM             position(554:563)   DATE 'YYYY-MM-DD',
  EINSATZTERMINSCHLUESSEL   position(586:615)   CHAR(30) "TRIM(RTRIM(LTRIM(:EINSATZTERMINSCHLUESSEL, '.'), '.'))",
  ENTFALLTERMINSCHLUESSEL   position(616:645)   CHAR(30) "TRIM(RTRIM(LTRIM(:ENTFALLTERMINSCHLUESSEL, '.'), '.'))",
  BAUKASTEN_KZ              position(1030:1030) CHAR(1),
  BAUKASTEN_ST              position(1031:1031) CHAR(1),
  BAUKASTEN_NODE_ID         position(1032:1063) CHAR(32),
  ENTFALL_DATUM             position(1120:1129) DATE 'YYYY-MM-DD'

)
