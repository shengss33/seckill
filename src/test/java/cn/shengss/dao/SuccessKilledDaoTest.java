package cn.shengss.dao;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.shengss.entity.SuccessKilled;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {
	
	@Resource
	private SuccessKilledDao dao;
	@Test
	public void testInsertSuccessKilled() {
		long seckillId=1000;
		long userPhone=13871152784l;
		int insertCount=dao.insertSuccessKilled(seckillId, userPhone);
		System.out.println("插入数量:"+insertCount);
	}

	@Test
	public void testQueryByIdWithSeckill() {
		long seckillId=1000;
		long userPhone=13871152784l;
		SuccessKilled successKilled=dao.queryByIdWithSeckill(seckillId, userPhone);
		System.out.println(successKilled);
	}

}
