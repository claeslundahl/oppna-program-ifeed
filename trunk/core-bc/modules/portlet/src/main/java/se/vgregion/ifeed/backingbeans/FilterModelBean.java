package se.vgregion.ifeed.backingbeans;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.ifeed.types.FieldInf;

import java.io.Serializable;

/**
* Created by Monica on 2014-06-03.
*/
@Component(value = "filterModelBean")
@Scope("session")
public class FilterModelBean implements Serializable {
   private String filterValue;

  private FieldInf fieldInf;


   public FilterModelBean() {
   }

   public String getFilterValue() {
       return filterValue;
   }

   public void setFilterValue(String filterValue) {
       this.filterValue = filterValue;
   }

   public FieldInf getFieldInf() {
       return fieldInf;
   }

   public void setFieldInf(FieldInf fieldInf) {
       this.fieldInf = fieldInf;
   }
}