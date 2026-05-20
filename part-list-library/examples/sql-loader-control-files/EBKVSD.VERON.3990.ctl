OPTIONS (SKIP=1)
LOAD DATA
APPEND
INTO TABLE TI_WH_EBK_VSD
(
  TI_WH_EBK_VSD_ID          "SEQ_TI_WH_EBK_VSD.NEXTVAL",
  TI_WH_IMPORT_ID           "3990",
  IMPORT_DATE               "CURRENT_TIMESTAMP",
  
  BAUKASTEN_NODE_ID         position(1:32)      CHAR(32),
  EINSATZTERMINSCHLUESSEL   position(114:143)   CHAR(30) "TRIM(:EINSATZTERMINSCHLUESSEL)",
  EINSATZ_DATUM             position(144:153)   DATE 'YYYY-MM-DD',
  ENTFALLTERMINSCHLUESSEL   position(154:183)   CHAR(30) "TRIM(:ENTFALLTERMINSCHLUESSEL)",
  ENTFALL_DATUM             position(184:193)   DATE 'YYYY-MM-DD',
  TEILENUMMER_PARENT        position(194:211)   CHAR(18),
  TNR_VORNUMMER_PARENT      position(194:196)   CHAR(3),
  TNR_MITTELGRUPPE_PARENT   position(197:199)   CHAR(3),
  TNR_ENDNUMMER_PARENT      position(200:202)   CHAR(3),
  TNR_INDEX_PARENT          position(203:204)   CHAR(2),
  BAUKASTEN_ST              position(212:212)   CHAR(1),
  TEILENUMMER               position(233:250)   CHAR(18),
  TNR_VORNUMMER             position(233:235)   CHAR(3),
  TNR_MITTELGRUPPE          position(236:238)   CHAR(3),
  TNR_ENDNUMMER             position(239:241)   CHAR(3),
  TNR_INDEX                 position(242:243)   CHAR(2),
  MENGENEINHEIT             position(251:253)   CHAR(3),  
  ANZAHL                    position(254:263)   CHAR(10),
  WAHLWEISE_FALL            position(269:272)   CHAR(4),
  WAHLWEISE_NR              position(273:274)   CHAR(2),
  KONSTRUKTIONSGRUPPE       position(281:281)   CHAR(1),
  KOSTENGRUPPE              position(283:286)   CHAR(4),
  SET_KZ                    position(287:289)   CHAR(3),
  PRODUKTSTRUKTUR           position(290:292)   CHAR(3),
  PRNR_REGEL                position(293:372)   CHAR(80),
  MEE                       position(375:375)   CHAR(1),
  AGGREGAT                  position(376:379)   CHAR(4),
  ENTWICKLUNGSSTAND_DT      position(398:407)   DATE 'YYYY-MM-DD',
  SORT_                     position(448:455)   INTEGER EXTERNAL,
  VWS                       position(456:464)   CHAR(9),
  NODE_LABEL                position(493:552)   CHAR(60)

)
