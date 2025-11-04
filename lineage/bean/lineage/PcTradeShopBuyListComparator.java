package lineage.bean.lineage;

import java.util.Comparator;

import lineage.bean.database.PcTradeShop;

public class PcTradeShopBuyListComparator implements Comparator<PcTradeShop> {
	
    @Override
    public int compare(PcTradeShop pts1, PcTradeShop pts2) {
    	try {
    		if (pts1.getEnLevel() == pts2.getEnLevel()) {
                if (pts1.getPrice() < pts2.getPrice()) {
                    return 1;
                } else if (pts1.getPrice() > pts2.getPrice()) {
                	 return -1;
                }
    		} else if (pts1.getEnLevel() < pts2.getEnLevel()) {
                return 1;
            } else if (pts1.getEnLevel() > pts2.getEnLevel()) {
                return -1;
            }
    	} catch (Exception e) {
			e.printStackTrace();
		}
        
        return 0;
    }
}
