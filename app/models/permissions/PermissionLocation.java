package models.permissions;

public enum PermissionLocation {
  local(1,"local"),
  home(2,"home"),
  always(3,"always"),
  never(0,"never");
  public final String name;
  public final int weight;

  PermissionLocation(int weight, String name) {
    this.weight = weight;
    this.name = name;
  }
}
