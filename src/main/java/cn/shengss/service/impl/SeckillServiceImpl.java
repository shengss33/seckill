package cn.shengss.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import cn.shengss.dao.SeckillDao;
import cn.shengss.dao.SuccessKilledDao;
import cn.shengss.dto.Exposer;
import cn.shengss.dto.SeckillExecution;
import cn.shengss.entity.Seckill;
import cn.shengss.entity.SuccessKilled;
import cn.shengss.enums.SeckillStatEnum;
import cn.shengss.exception.RepeatKillException;
import cn.shengss.exception.SeckillCloseException;
import cn.shengss.exception.SeckillException;
import cn.shengss.service.SeckillService;

@Service
public class SeckillServiceImpl implements SeckillService {
	
	//日志对象
	private Logger logger=LoggerFactory.getLogger(this.getClass());
	
	//加盐
	private final String salt="ss";
	
	//注入service
	@Autowired
	private SeckillDao dao;
	@Resource
	private SuccessKilledDao succDao;
	@Override
	public List<Seckill> getSeckillList() {
		
		return dao.queryAll(0, 4);
	}

	@Override
	public Seckill getById(long seckillId) {
		
		return dao.queryById(seckillId);
	}

	@Override
	public Exposer exportSeckillUrl(long seckillId) {
		Seckill seckill=dao.queryById(seckillId);
		//
		if(seckill==null){
			return new Exposer(false, seckillId);
		}
		//秒杀未开启
		Date sTime=seckill.getStartTime();
		Date eTime=seckill.getEndTime();
		Date now =new Date();
		if(sTime.getTime()>now.getTime()||eTime.getTime()<now.getTime()){
			return new Exposer(false,seckillId,now.getTime(),sTime.getTime(),eTime.getTime());
		}
		//秒杀开启，返回秒杀商品的id、用给接口加密的md5
        String md5=getMD5(seckillId);
        return new Exposer(true,md5,seckillId);
	}
	private String getMD5(long seckillId)
    {
        String base=seckillId+"/"+salt;
        String md5= DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
	
	//秒杀是否成功，成功:减库存，增加明细；失败:抛出异常，事务回滚
	@Transactional
	   /**
	    * 使用注解控制事务方法的优点:
	    * 1.开发团队达成一致约定，明确标注事务方法的编程风格
	    * 2.保证事务方法的执行时间尽可能短，不要穿插其他网络操作RPC/HTTP请求或者剥离到事务方法外部
	    * 3.不是所有的方法都需要事务，如只有一条修改操作、只读操作不要事务控制
	    */
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
			throws SeckillException, RepeatKillException, SeckillCloseException {
		 if (md5==null||!md5.equals(getMD5(seckillId)))
	        {
	            throw new SeckillException("seckill data rewrite");//秒杀数据被重写了
	        }
	        //执行秒杀逻辑:减库存+增加购买明细
	        Date nowTime=new Date();

	        try{
	            //减库存
	            int updateCount=dao.reduceNumber(seckillId,nowTime);
	            if (updateCount<=0)
	            {
	                //没有更新库存记录，说明秒杀结束
	                throw new SeckillCloseException("seckill is closed");
	            }else {
	                //否则更新了库存，秒杀成功,增加明细
	                int insertCount=succDao.insertSuccessKilled(seckillId,userPhone);
	                //看是否该明细被重复插入，即用户是否重复秒杀
	                if (insertCount<=0)
	                {
	                    throw new RepeatKillException("seckill repeated");
	                }else {
	                    //秒杀成功,得到成功插入的明细记录,并返回成功秒杀的信息
	                    SuccessKilled successKilled=succDao.queryByIdWithSeckill(seckillId,userPhone);
	                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS,successKilled);	                }
	            }

	        }catch (SeckillCloseException e1)
	        {
	            throw e1;
	        }catch (RepeatKillException e2)
	        {
	            throw e2;
	        }catch (Exception e)
	        {
	            logger.error(e.getMessage(),e);
	            //所以编译期异常转化为运行期异常
	            throw new SeckillException("seckill inner error :"+e.getMessage());
	        }

	    }
	

}
