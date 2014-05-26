package se.vgregion.ifeed.backingbeans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.ifeed.service.ifeed.IFeedService;
import se.vgregion.ifeed.types.FieldsInf;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Monica on 2014-04-15.
 */
@Component(value = "fieldInfsBackingBean")
@Scope("request")
public class FieldInfsBackingBean implements Serializable{

    @Autowired
    private IFeedService iFeedService;
    private FieldsInf fieldsInf;
    private String fieldsInfText;

    public FieldInfsBackingBean() {
    }

    private FieldsInf getFieldsInf() {

        List<FieldsInf> infs = iFeedService.getFieldsInfs();
        if (infs.isEmpty()) {
            return new FieldsInf();
        }
        return infs.get(infs.size() - 1);

    }

    public void save(FieldsInf fields) {

        fields.getFieldInfs();   // Throws exception and aborts the save if the text field cannot be parsed to
                                 // FieldInf-objects.
        iFeedService.storeFieldsInf(fields);
    }

    public String getFieldsInfText() {
        List<FieldsInf> infs = iFeedService.getFieldsInfs();
        fieldsInfText = infs.get(infs.size() - 1).getText();

        return fieldsInfText;
    }

    public void setFieldsInfText(String fieldsInfText) {
        this.fieldsInfText = fieldsInfText;
    }

    public void setFieldsInf(FieldsInf fieldsInf) {
        this.fieldsInf = fieldsInf;
    }

    public IFeedService getiFeedService() {
        return iFeedService;
    }

    public void setiFeedService(IFeedService iFeedService) {
        this.iFeedService = iFeedService;
    }
}
