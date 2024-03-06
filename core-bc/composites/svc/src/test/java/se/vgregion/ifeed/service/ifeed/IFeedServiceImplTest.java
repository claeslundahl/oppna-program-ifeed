package se.vgregion.ifeed.service.ifeed;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import se.vgregion.dao.domain.patterns.repository.db.jpa.JpaRepository;
import se.vgregion.ifeed.types.FieldsInf;
import se.vgregion.ifeed.types.IFeed;
import se.vgregion.ifeed.types.VgrDepartment;
import se.vgregion.ifeed.types.VgrGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IFeedServiceImplTest {

	IFeedServiceImpl service;
	private JpaRepository<IFeed, Long, Long> iFeedRepoParam;
	private JpaRepository<FieldsInf, Long, Long> fieldsInfParam;

	@Before
	public void setUp() throws Exception {
		iFeedRepoParam = mock(JpaRepository.class);
		JpaRepository<VgrDepartment, Long, Long> departmentRepo = mock(JpaRepository.class);
        service = new IFeedServiceImpl(iFeedRepoParam, fieldsInfParam, departmentRepo);
	}

	@Ignore
	@Test
	public void getIFeed() {
		Long id = 101l;
		IFeed value = new IFeed();
		value.setId(id);
		when(iFeedRepoParam.find(id)).thenReturn(value);
		IFeed result = service.getIFeed(id);
		Assert.assertEquals(id, value.getId());
	}

}
