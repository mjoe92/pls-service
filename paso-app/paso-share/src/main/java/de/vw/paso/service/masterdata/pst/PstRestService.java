package de.vw.paso.service.masterdata.pst;

public interface PstRestService {
  String URL = "/api/pst";
  String ADD = "/add";
  String EDIT = "/edit";
  String DELETE = "/delete";

  PstListDTO getPsts();

  void deletePst(Long id);

  PstDTO editPst(PstDTO pstDTO);

  PstDTO addPst(PstDTO pstDTO);
}
