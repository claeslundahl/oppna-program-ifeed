package se.vgregion.ifeed.service.kiv;


public class UnitSearchServiceTest {

    public static void main(String[] args) {
        UnitSearchService uss = UnitSearchService.newInstanceFromConfig();

        uss.update();
        System.out.println(uss.getUnitsRoot().getUnits().size());
        System.out.println(uss.getRolesRoot().getRoles().size());
    }

}
