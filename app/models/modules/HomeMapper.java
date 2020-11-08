package models.modules;

import models.Location;
import models.User;
import models.devices.Connection;
import models.devices.Device;
import models.devices.Door;
import models.devices.Light;

import java.util.List;
import java.util.Map;

public class HomeMapper {
  private static Node[][] map;
  private static final String topLeftWall = "<td class=\"wall-left wall-top\"></td>";
  private static final String topWall = "<td class=\"wall-top\"></td>";
  private static final String leftWall = "<td class=\"wall-left\"></td>";
  private static final String noWall = "<td></td>";

  public static void mapHome(int[][] locationMatrix, List<Location> locationList) {
    int row = locationMatrix.length;
    int col = locationMatrix[0].length;
    map = new Node[row+1][col+1];
    for (int i = 0; i <= row; i++) {
      for (int j = 0; j <= col; j++) {
        map[i][j] = new Node(SHS.getOutside());
      }
    }
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        map[i][j] = new Node(locationList.get(locationMatrix[i][j]));
      }
    }

    for (int i = 0; i <= row; i++) {
      for (int j = 0; j <= col; j++) {
        Location location = map[i][j].location;
        Location toCheck;
        if (j == col) {
          if (location.getLocationType() == Location.LocationType.Outside) {
            map[i][j].right = Element.connection;
          }
        } else {
          // Check Right
          toCheck = map[i][j + 1].location;
          if (toCheck == location) {
            map[i][j].right = Element.connection;
          } else if (location.getLocationType().equals(Location.LocationType.Outside) && toCheck.getLocationType().equals(Location.LocationType.Outside)) {
            map[i][j].right = Element.connection;
          } else {
            for (Device device : location.getDeviceMap().values()) {
              if (device == toCheck.getDeviceMap().get(device.getName())) {
                if (device instanceof Door) {
                  map[i][j].right = Element.door;
                  map[i][j+1].leftDoor = (Door)device;
                } else if (device instanceof Connection) {
                  map[i][j].right = Element.connection;
                }
              } else if ((device instanceof Door) && map[i][j+1].location == SHS.getOutside()){
                Door door = (Door)device;
                Location secondLocation = door.getSecondLocation();
                if (locationList.contains(secondLocation) && secondLocation.getLocationType().equals(Location.LocationType.Outside)) {
                  locationList.remove(secondLocation);
                  map[i][j+1].location = secondLocation;
                  map[i][j].right = Element.door;
                  map[i][j+1].leftDoor = (Door)device;
                }
              }
            }
          }
        }
        if (i == row) {
          if (location == SHS.getOutside()) {
            map[i][j].bottom = Element.connection;
          }
        } else {
          // Check Bottom
          toCheck = map[i+1][j].location;
          if (toCheck == location) {
            map[i][j].bottom = Element.connection;
          } else if (location.getLocationType().equals(Location.LocationType.Outside) && toCheck.getLocationType().equals(Location.LocationType.Outside)) {
            map[i][j].bottom = Element.connection;
          } else {
            for (Device device : location.getDeviceMap().values()) {
              if (device == toCheck.getDeviceMap().get(device.getName())) {
                if (device instanceof Door) {
                  map[i][j].bottom = Element.door;
                  map[i+1][j].topDoor = (Door)device;
                } else if (device instanceof Connection) {
                  map[i][j].bottom = Element.connection;
                }
              } else if ((device instanceof Door) && map[i+1][j].location == SHS.getOutside()){
                Door door = (Door)device;
                Location secondLocation = door.getSecondLocation();
                if (locationList.contains(secondLocation) && secondLocation.getLocationType().equals(Location.LocationType.Outside)) {
                  locationList.remove(secondLocation);
                  map[i+1][j].location = secondLocation;
                  map[i][j].bottom = Element.door;
                  map[i+1][j].topDoor = (Door)device;
                }
              }
            }
          }
        }
        if (i == 0) {
          if (location.getLocationType() == Location.LocationType.Outside) {
            map[i][j].top = Element.connection;
          }
        } else {
          // Check Top
          toCheck = map[i-1][j].location;
          if (toCheck == location) {
            map[i][j].top = Element.connection;
          } else if (location.getLocationType().equals(Location.LocationType.Outside) && toCheck.getLocationType().equals(Location.LocationType.Outside)) {
            map[i][j].top = Element.connection;
          } else {
            for (Device device : location.getDeviceMap().values()) {
              if (device == toCheck.getDeviceMap().get(device.getName())) {
                if (device instanceof Door) {
                  map[i][j].top = Element.door;
                } else if (device instanceof Connection) {
                  map[i][j].top = Element.connection;
                }
              }
            }
          }
        }
        if (j == 0) {
          if (location.getLocationType() == Location.LocationType.Outside) {
            map[i][j].left = Element.connection;
          }
        } else {
          // Check Left
          toCheck = map[i][j-1].location;
          if (toCheck == location) {
            map[i][j].left = Element.connection;
          } else if (location.getLocationType().equals(Location.LocationType.Outside) && toCheck.getLocationType().equals(Location.LocationType.Outside)) {
            map[i][j].left = Element.connection;
          } else {
            for (Device device : location.getDeviceMap().values()) {
              if (device == toCheck.getDeviceMap().get(device.getName())) {
                if (device instanceof Door) {
                  map[i][j].left = Element.door;
                } else if (device instanceof Connection) {
                  map[i][j].left = Element.connection;
                }
              }
            }
          }
        }
      }
    }
  }

  private static class Node {
    Element top;
    Door topDoor;
    Element bottom;
    Element left;
    Door leftDoor;
    Element right;
    Location location;

    Node(Location location) {
      this.location = location;
      top = Element.wall;
      bottom = Element.wall;
      left = Element.wall;
      right = Element.wall;
    }
  }
  private enum Element {
    wall,
    connection,
    door
  }

  public static int getWidth() {
    return (map != null)? map.length : 1;
  }

  public static int getHeight() {
    return (map != null)? map[0].length : 1;
  }

  private static String getDoor(Door door, char cardinality) {
    String doorClass = "";
    switch (door.getStatus()) {
      case Door.statusOpen:
        switch (cardinality) {
          case 'T':
            doorClass = "door-left";
            break;
          case 'L':
            doorClass = "door-top";
            break;
          case 'B':
          case 'R': //nothing to draw
        }
        break;
      default:
        switch (cardinality) {
          case 'T':
            doorClass = "door-top";
            break;
          case 'L':
            doorClass = "door-left";
            break;
          case 'B':
          case 'R': //nothing to draw
        }
        break;
    }
    return "<td class='" + doorClass + "' onClick='toggleDoorControl(\"" + door.getLocation().getName() + "\", \"" + door.getName() + "\", \"" + door.getStatus() + "\")'></td>";
    //return "<td class='" + doorClass + "' data-toggle='tooltip' data-placement='left' title='" + door.getName() + "' onClick='toggleDoorControl(\"" + door.getLocation().getName() + "\", \"" + door.getName() + "\", \"" + door.getStatus() + "\")'></td>";
  }

  private static String getPeople(Node node) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("<td>");
    Map<String,User> userMap = node.location.getUserMap();
    if ((userMap.size() > 0) && (node.location != SHS.getOutside())) {
      stringBuilder.append("<i class='fas fa-users' data-toggle='tooltip' data-html='true' title='");
      for (User user : userMap.values()) {
        stringBuilder.append("[<em>");
        stringBuilder.append(user.getType());
        stringBuilder.append("</em>] ");
        stringBuilder.append(user.getName());
        stringBuilder.append("<br />");
      }
      stringBuilder.append("'></i>");
    }
    stringBuilder.append("</td>");
    return stringBuilder.toString();
  }

  private static String getLight(Node node) {
    if (node.location == SHS.getOutside()) {
      if ((node.top == Element.connection) && (node.left == Element.connection)) {
        return noWall;
      } else if ((node.top != Element.connection) && (node.left != Element.connection)) {
        return topLeftWall;
      } else {
        return node.top != Element.connection? topWall : leftWall;
      }
    }

    Light light = (Light)node.location.getDeviceMap().get(node.location.getName() + " Light");
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append("<td");
    if ((node.top == Element.connection) && (node.left == Element.connection)) {
      stringBuilder.append(">");
    } else if ((node.top != Element.connection) && (node.left != Element.connection)) {
      stringBuilder.append(" class='wall-left wall-top'>");
    } else {
      stringBuilder.append(" class='");
      stringBuilder.append(node.top != Element.connection? "wall-top'>" : "wall-left'>");
    }
    stringBuilder.append("<i class='fas fa-lightbulb");
    stringBuilder.append(light.getStatus().equals(Light.statusOn)? " text-warning" : " text-secondary");
    stringBuilder.append("' onclick='toggleLightControl(\"");
    stringBuilder.append(node.location.getName());
    stringBuilder.append("\", \"");
    stringBuilder.append(light.getName());
    stringBuilder.append("\", \"");
    stringBuilder.append(light.getStatus());
    stringBuilder.append("\")'></i>");
    return stringBuilder.toString();
  }

  public static String toHtml() {
    if (map == null) {
      return "";
    }
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("<table class=\"houseGrid\">");
    for (int i = 0; i < map.length; i++) {
      stringBuilder.append("<tr>");
      for (int j = 0; j < map[0].length; j++) {
        if (map[i][j].location == SHS.getOutside()) {
          stringBuilder.append("<td><table class=\"houseNode\"><tr>");
        } else {
          stringBuilder.append("<td><table class=\"houseNode\" data-toggle=\"tooltip\" data-placement=\"bottom\" title=\"");
          stringBuilder.append(map[i][j].location.getName());
          stringBuilder.append("\"><tr>");
        }
        stringBuilder.append(getLight(map[i][j]));
 /*       if ((map[i][j].top == Element.connection) && (map[i][j].left == Element.connection)) {
          stringBuilder.append(noWall);
        } else if ((map[i][j].top != Element.connection) && (map[i][j].left != Element.connection)) {
          stringBuilder.append(topLeftWall);
        } else {
          stringBuilder.append(map[i][j].top != Element.connection? topWall : leftWall);
        }*/
        if (map[i][j].top == Element.connection) {
          stringBuilder.append(noWall);
        } else if (map[i][j].top == Element.wall) {
          stringBuilder.append(topWall);
        } else {
          //stringBuilder.append((map[i][j].topDoor.getStatus().equals(Door.statusOpen))? leftDoor : topDoor);
          stringBuilder.append(getDoor(map[i][j].topDoor, 'T'));
        }
        if (map[i][j].top != Element.connection) {
          stringBuilder.append(topWall);
        } else {
          stringBuilder.append(noWall);
        }

        stringBuilder.append("</tr><tr>");
        if (map[i][j].left == Element.connection) {
          stringBuilder.append(noWall);
        } else if (map[i][j].left == Element.wall){
          stringBuilder.append(leftWall);
        } else {
          //stringBuilder.append((map[i][j].leftDoor.getStatus().equals(Door.statusOpen))? topDoor : leftDoor);
          stringBuilder.append(getDoor(map[i][j].leftDoor, 'L'));
        }
        //stringBuilder.append(noWall);
        stringBuilder.append(getPeople(map[i][j]));
        //stringBuilder.append(noWall);
        if (map[i][j].right == Element.door) {
          stringBuilder.append(getDoor(map[i][j+1].leftDoor, 'R'));
        } else {
          stringBuilder.append(noWall);
        }

        stringBuilder.append("</tr><tr>");
        if (map[i][j].left != Element.connection) {
          stringBuilder.append(leftWall);
        } else {
          stringBuilder.append(noWall);
        }
        //stringBuilder.append(noWall);
        if (map[i][j].bottom == Element.door) {
          stringBuilder.append(getDoor(map[i+1][j].topDoor, 'B'));
        } else {
          stringBuilder.append(noWall);
        }
        stringBuilder.append(noWall);





        stringBuilder.append("</tr></table></td>");
      }
      stringBuilder.append("</tr>");
    }
    stringBuilder.append("</tbody></table>");
    return stringBuilder.toString();
  }
}
