OPTIONS (SKIP=1)
LOAD DATA
APPEND
INTO TABLE TI_WH_NODE
(
  TI_WH_NODE_ID        "SEQ_TI_WH_NODE.NEXTVAL",
  TI_WH_IMPORT_ID      "3990",
  IMPORT_DATE          "CURRENT_TIMESTAMP",
  
  PROD                 position(1:4)     CHAR(4) "TRIM(:PROD)",
  NODE_PARENT_ID       position(5:36)    CHAR(32),
  NODE_ID              position(37:68)   CHAR(32),
  SORT_                position(80:85)   INTEGER EXTERNAL,
  NODE_TYPE            position(98:105)  CHAR(8),
  NODE_VALUE           position(146:185) CHAR(40),
  NODE_LABEL           position(246:305) CHAR(60)

)
