package lineage.bean.lineage;

import java.util.Comparator;

public class PcTradeShopBuyComparator implements Comparator<PcTradeShopBuy> {
	
    @Override
    public int compare(PcTradeShopBuy ptsb1, PcTradeShopBuy ptsb2) {
    	try {
            if (ptsb1.getCount() < ptsb2.getCount()) {
                return 1;
            } else if (ptsb1.getCount() > ptsb2.getCount()) {
                return -1;
            }
    	} catch (Exception e) {
			e.printStackTrace();
		}
        
        return 0;
    }
}
