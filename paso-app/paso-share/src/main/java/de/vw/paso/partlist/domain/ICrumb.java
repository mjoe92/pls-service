package de.vw.paso.partlist.domain;

public interface ICrumb {

  ICrumb getCrumbParent();

  String getCrumbText();
}
