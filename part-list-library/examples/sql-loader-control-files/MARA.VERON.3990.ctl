OPTIONS (SKIP=1, ERRORS=1000000000)
LOAD DATA
APPEND
INTO TABLE TI_WH_MARA
(
  TI_WH_MARA_ID                 "SEQ_TI_WH_MARA.NEXTVAL",
  TI_WH_IMPORT_ID               "3990",
  IMPORT_DATE                   "CURRENT_TIMESTAMP",
  
  TEILENUMMER                   position(1:18)    CHAR(18),
  TNR_VORNUMMER                 position(1:3)     CHAR(3),
  TNR_MITTELGRUPPE              position(4:6)     CHAR(3),
  TNR_ENDNUMMER                 position(7:9)     CHAR(3),
  TNR_INDEX                     position(10:11)   CHAR(2),
  
  GEWICHT_GESCHAETZT_FE         position(49:57)   DECIMAL EXTERNAL,
  GEWICHT_BERECHNET_FE          position(69:77)   DECIMAL EXTERNAL,
  GEWICHT_GEWOGEN_FE            position(89:97)   DECIMAL EXTERNAL,
  GEWICHT_GEWOGEN_PROD          position(111:123) CHAR(13) "TO_NUMBER(:GEWICHT_GEWOGEN_PROD, '999999999D999', 'NLS_NUMERIC_CHARACTERS = '', ''')",
  
  ZSB_KZ                        position(134:134) CHAR(1),
  KONSTRUKTIONSSTAND            position(136:141) CHAR(6),
  ZEICHNUNG_DT                  position(174:183) DATE 'YYYY-MM-DD',
  GRUNDSTOFF                    position(185:187) CHAR(3),
  QUALITAET                     position(188:227) CHAR(40),
  KONSTRUKTIONSVERANTWORTUNG1   position(252:252) CHAR(1),
  KONSTRUKTIONSVERANTWORTUNG2   position(253:253) CHAR(1),
  RECYCLEBAR                    position(254:254) CHAR(1),
  ZEICHNUNG_ST                  position(570:571) CHAR(2),
  BEZEICHNUNG2                  position(583:600) CHAR(18)
  
)
