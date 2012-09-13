package se.vgregion.ifeed.formbean;

import java.util.AbstractList;
import java.util.List;

import org.springframework.web.util.UriTemplate;

import se.vgregion.ifeed.types.IFeed;

/**
 * @author Anders Asplund
 *
 */
public class IFeedFormBeanList extends AbstractList<IFeedFormBeanList.IFeedFormBean>{

    private UriTemplate iFeedAtomFeed;
    private List<IFeed> iFeedList;

    public class IFeedFormBean {
        private static final long serialVersionUID = 1L;
        private IFeed iFeed;
        public IFeedFormBean(IFeed iFeed) {
            this.iFeed = iFeed;
        }

        public String getAtomLink() {
            return iFeedAtomFeed.expand(iFeed.getId(), iFeed.getSortField(), iFeed.getSortDirection()).toString();
        }

        public IFeed getiFeed() {
            return iFeed;
        }
    }

    public IFeedFormBeanList(List<IFeed> iFeedList, UriTemplate iFeedAtomFeed) {
        this.iFeedList = iFeedList;
        this.iFeedAtomFeed = iFeedAtomFeed;
    }

    @Override
    public IFeedFormBean get(int index) {
        return new IFeedFormBean(iFeedList.get(index));
    }

    @Override
    public int size() {
        return iFeedList.size();
    }
}
