package org.jeecgframework.web.system.sms.util.task;

import com.zzjee.md.entity.MdBinEntity;
import com.zzjee.md.entity.MdCusEntity;
import com.zzjee.md.entity.MdGoodsEntity;
import com.zzjee.wm.entity.WmDayCostConfEntity;
import com.zzjee.wm.entity.WmDayCostEntity;
import com.zzjee.wm.entity.WmToMoveGoodsEntity;
import com.zzjee.wm.entity.WvStockEntity;
import org.jeecgframework.core.constant.Globals;
import org.jeecgframework.core.util.DateUtils;
import org.jeecgframework.core.util.ResourceUtil;
import org.jeecgframework.core.util.StringUtil;
import org.jeecgframework.web.system.service.SystemService;
import org.jeecgframework.web.system.sms.service.TSSmsServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;

/**
 * 
 * @ClassName:SmsSendTask 所有信息的发送定时任务类
 * @Description: TODO
 * @date 2014-11-13 下午5:06:34
 * 
 */
@Service("goodsMoveTask")
public class GoodsMoveTask {


	@Autowired
	private SystemService systemService;

	/* @Scheduled(cron="0 0 01 * * ?") */
	public void run() {
		long start = System.currentTimeMillis();
		String datestr = DateUtils.date2Str(DateUtils.date_sdf);
		org.jeecgframework.core.util.LogUtil
				.info("===================转移定时任务开始===================");
		String moveStats = ResourceUtil.getConfigByName("moveStats");
		String binStoress = ResourceUtil.getConfigByName("binStoress");

		if(StringUtil.isEmpty(moveStats)){
			moveStats = "计划中";
		}
		if(StringUtil.isNotEmpty(binStoress)){
			String binStoressa[] = binStoress.split(",");
			for(String binstore:binStoressa){
				this.goodsMove(binstore,moveStats);
			}
		}



		org.jeecgframework.core.util.LogUtil
				.info("===================转移定时任务结束===================");
		long end = System.currentTimeMillis();
		long times = end - start;
		org.jeecgframework.core.util.LogUtil.info("转移定时任务总耗时" + times + "毫秒");
	}
	public  void goodsMove(String binstrore,String moveStatus ){

		String tsql = "";
		List<Map<String, Object>> resulmove = systemService
				.findForJdbc(tsql, binstrore);
		//生成任务
		for (int i = 0; i < resulmove.size(); i++) {

				WvStockEntity t = systemService.get(WvStockEntity.class,resulmove.get(i).get("id").toString());
				try {
					WmToMoveGoodsEntity wmtomove = new WmToMoveGoodsEntity();
					wmtomove.setOrderTypeCode("TPZY");
					wmtomove.setBinFrom(t.getKuWeiBianMa());
					wmtomove.setBinTo(t.getKuWeiBianMa());
					wmtomove.setCusCode(t.getCusCode());
					wmtomove.setCusName(t.getZhongWenQch());
					wmtomove.setToCusCode(t.getCusCode());
					wmtomove.setToCusName(t.getZhongWenQch());
					wmtomove.setGoodsId(t.getGoodsId());
					wmtomove.setGoodsName(t.getShpMingCheng());
					wmtomove.setGoodsProData(t.getGoodsProData());
					wmtomove.setGoodsQua(t.getGoodsQua().toString());
					wmtomove.setGoodsUnit(t.getGoodsUnit());
					wmtomove.setBaseGoodscount(t.getGoodsQua().toString());
					wmtomove.setBaseUnit(t.getGoodsUnit());
					wmtomove.setMoveSta(moveStatus);
					wmtomove.setTinFrom(t.getBinId());
					wmtomove.setTinId("B");
					systemService.save(wmtomove);
				}catch (Exception e){
				}



			}

	}
}
