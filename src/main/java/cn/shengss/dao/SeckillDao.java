package cn.shengss.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.shengss.entity.Seckill;

public interface SeckillDao {
	/**
     * �����
     * @param seckillId
     * @param killTime
     * @return ���Ӱ������>1����ʾ���¿��ļ�¼����
     */
    int reduceNumber(@Param("seckillId")long seckillId, @Param("killTime")Date killTime);

    /**
     * ����id��ѯ��ɱ����Ʒ��Ϣ
     * @param seckillId
     * @return
     */
    Seckill queryById(long seckillId);

    /**
     * ����ƫ������ѯ��ɱ��Ʒ�б�
     * @param off
     * @param limit
     * @return
     */
    List<Seckill> queryAll(@Param("offset") int off,@Param("limit")int limit);

}
