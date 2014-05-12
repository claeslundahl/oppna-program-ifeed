package se.vgregion.ifeed.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Created by clalu4 on 2014-04-25.
 */
public interface Images extends ClientBundle {

    @Source("information.png")
    ImageResource information();

    @Source("utropstecken_rod.gif")
    ImageResource exclamation();

    @Source("loading.gif")
    ImageResource loading();

    @Source("sortasc.gif")
    ImageResource sortasc();

    @Source("sortdesc.gif")
    ImageResource sortdesc();

}
