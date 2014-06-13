package se.vgregion.ifeed.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Loads images as a image-map, efficient as described in the gwt documentation.
 */
public interface Images extends ClientBundle {

    /**
     * Gets image representing information / description about something.
     * @return a resource describing the image.
     */
    @Source("information.png")
    ImageResource information();

    /**
     * Gets a image showing a visage that denounce that something remarkable has happened.
     * @return a resource describing the image.
     */
    @Source("utropstecken_rod.gif")
    ImageResource exclamation();

    /**
     * Getter for image showing a wait or 'system is loading' representation.
     * @return a resource describing the image.
     */
    @Source("loading.gif")
    ImageResource loading();

    /**
     * Shows sorting asc.
     * @return a resource describing the image.
     */
    @Source("sortasc.gif")
    ImageResource sortasc();

    /**
     * Depicts something that sorts asc.
     * @return a resource describing the image.
     */
    @Source("sortdesc.gif")
    ImageResource sortdesc();

    /**
     * Depicts something that sorts asc.
     * @return a resource describing the image.
     */
    @Source("ajax-loader.gif")
    ImageResource ajaxLoader();

}
