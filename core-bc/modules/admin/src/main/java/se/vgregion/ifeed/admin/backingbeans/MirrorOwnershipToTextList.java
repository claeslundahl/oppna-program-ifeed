package se.vgregion.ifeed.admin.backingbeans;

import se.vgregion.ifeed.types.Ownership;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by clalu4 on 2014-05-27.
 */
public class MirrorOwnershipToTextList extends MirrorList<String, Ownership> implements Serializable {

    protected MirrorOwnershipToTextList(Collection<Ownership> source) {
        super(source);
    }

    @Override
    String toMirrorImage(Ownership original) {
        return original.getUserId();
    }

    @Override
    Ownership toRealItem(String original) {
        Ownership result = new Ownership();
        result.setUserId(original);
        return result;
    }
}
