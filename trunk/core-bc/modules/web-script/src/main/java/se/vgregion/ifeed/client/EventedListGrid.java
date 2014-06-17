package se.vgregion.ifeed.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * Created by clalu4 on 2014-06-13.
 */
public class EventedListGrid<T> extends Composite {

    protected FlexTable impl = new FlexTable();

    protected EventedList<T> data;

    protected int rowOffset = 0;

    public EventedListGrid() {
        this(new EventedList<T>());
    }

    public EventedListGrid(EventedList<T> data) {
        this.data = data;
        initWidget(impl);
        init();
    }

    protected void init() {
        each(data);
        data.getAddSpies().add(new EventedList.Spy<T>() {
            @Override
            public void event(T item, int index) {
                each(item, index + rowOffset);
            }

            @Override
            public boolean isRemoveAble() {
                return !EventedListGrid.this.isAttached();
            }
        });

        data.getRemoveSpies().add(new EventedList.Spy<T>() {
            @Override
            public void event(T item, int index) {
                impl.removeRow(index + rowOffset);
            }

            @Override
            public boolean isRemoveAble() {
                return !EventedListGrid.this.isAttached();
            }
        });
    }

    public void each(Iterable<T> items) {
        int row = rowOffset;
        for (T item: items) each(item, row++);
    }

    public void each(T item, int row) {
        impl.setWidget(row, 0, new Label(item.toString()));
    }

    public EventedList<T> getData() {
        return data;
    }
}
