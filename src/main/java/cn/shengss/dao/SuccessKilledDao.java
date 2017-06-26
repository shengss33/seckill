package cn.shengss.dao;

import org.apache.ibatis.annotations.Param;

import cn.shengss.entity.SuccessKilled;

public interface SuccessKilledDao {
	/**
     * ���빺����ϸ,�ɹ����ظ�
     * @param seckillId
     * @param userPhone
     * @return���������
     */
    int insertSuccessKilled(@Param("seckillId")long seckillId,@Param("userPhone")long userPhone);


    /**
     * ������ɱ��Ʒ��id��ѯ��ϸSuccessKilled����(�ö���Я����Seckill��ɱ��Ʒ����)
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId")long seckillId,@Param("userPhone")long userPhone);
}
