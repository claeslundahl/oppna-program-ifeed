package se.vgregion.ifeed.shared;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vgr_ifeed_dynamic_table_sorting")
public class DynamicTableSortingDef extends AbstractEntity {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  protected Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fk_table_def_id")
  private DynamicTableDef tableDef;

  private String name;

  private Integer index;

  private String direction;

  @Override
  String toStringImpl() {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    sb.append(quoute("name") + ":" + quoute(getName()) + ", ");
    sb.append(quoute("index") + ":" + quoute(getIndex()) + ", ");
    sb.append(quoute("direction") + ":" + quoute(getDirection()));
    sb.append("}");
    return sb.toString();
  }

  private String quoute(Object s) {
    if (s == null) {
      return "null";
    }
    return "\"" + s + "\"";
  }

  @Override
  Long getId() {
    return id;
  }

  @Override
  void setId(Long id) {
    this.id = id;
  }

  public DynamicTableDef getTableDef() {
    return tableDef;
  }

  public void setTableDef(DynamicTableDef tableDef) {
    this.tableDef = tableDef;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getIndex() {
    return index;
  }

  public void setIndex(Integer index) {
    this.index = index;
  }

  public void setDirection(String direction) {
    this.direction = direction;
  }

  public String getDirection() {
    return direction;
  }

  public static void main(String[] args) {
    List<DynamicTableSortingDef> items = new ArrayList<>();
    DynamicTableSortingDef item = new DynamicTableSortingDef();
    item.setIndex(1);
    item.setName("dc.title");
    item.setDirection("asc");
    items.add(item);
    items.add(item);
    System.out.println(items);
  }
}
