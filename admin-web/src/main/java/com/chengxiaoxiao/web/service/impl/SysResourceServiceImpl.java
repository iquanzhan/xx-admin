package com.chengxiaoxiao.web.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.chengxiaoxiao.common.utils.IdWorker;
import com.chengxiaoxiao.model.common.dtos.result.CodeMsg;
import com.chengxiaoxiao.model.mappers.web.SysResourceMapper;
import com.chengxiaoxiao.model.mappers.web.SysRoleResourceMapper;
import com.chengxiaoxiao.model.repository.BaseDao;
import com.chengxiaoxiao.model.repository.SysResourceRepository;
import com.chengxiaoxiao.model.repository.SysRoleRepository;
import com.chengxiaoxiao.model.repository.SysRoleResourceRepository;
import com.chengxiaoxiao.model.web.dtos.query.sysresource.SysResourceModelDto;
import com.chengxiaoxiao.model.web.dtos.query.sysresource.SysResourceSearchDto;
import com.chengxiaoxiao.model.web.dtos.result.SysResourceTreeDto;
import com.chengxiaoxiao.model.web.dtos.result.SysRoleTreeDto;
import com.chengxiaoxiao.model.web.pojos.SysResource;
import com.chengxiaoxiao.model.web.pojos.SysRoleResource;
import com.chengxiaoxiao.web.exception.GlobleException;
import com.chengxiaoxiao.web.service.SysResourceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 资源信息处理
 *
 * @Author: Cheng XiaoXiao  (🍊 ^_^ ^_^)
 * @Date: 2020/2/16 10:06 下午
 * @Description:
 */
@Service
@SuppressWarnings("all")
public class SysResourceServiceImpl extends BaseServiceImpl<SysResource, String> implements SysResourceService {
    @Autowired
    private SysResourceRepository sysResourceRepository;
    @Autowired
    private SysRoleResourceRepository sysRoleResourceRepository;
    @Autowired
    private SysRoleRepository sysRoleRepository;

    @Autowired
    private SysRoleResourceMapper sysRoleResourceMapper;
    @Autowired
    private SysResourceMapper sysResourceMapper;

    @Autowired
    private IdWorker idWorker;

    @Override
    public BaseDao<SysResource, String> getBaseDao() {
        return this.sysResourceRepository;
    }

    @Override
    public Page search(SysResourceSearchDto sysResourceSearchDto, PageRequest pageRequest) {
        return getBaseDao().findAll(new Specification<SysResource>() {
            @Override
            public Predicate toPredicate(Root<SysResource> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                if (!StringUtils.isBlank(sysResourceSearchDto.getName())) {
                    Predicate predicate = criteriaBuilder.like(root.get("name").as(String.class), "%" + sysResourceSearchDto.getName() + "%");
                    list.add(predicate);
                }
                if (sysResourceSearchDto.getType() != null) {
                    Predicate predicate = criteriaBuilder.equal(root.get("type").as(String.class), sysResourceSearchDto.getType());
                    list.add(predicate);
                }

                if (!StringUtils.isBlank(sysResourceSearchDto.getSourceUrl())) {
                    Predicate predicate = criteriaBuilder.like(root.get("scourceKey").as(String.class), "%" + sysResourceSearchDto.getSourceUrl() + "%");
                    list.add(predicate);
                }

                if (!StringUtils.isBlank(sysResourceSearchDto.getSourceUrl())) {
                    Predicate predicate = criteriaBuilder.like(root.get("sourceUrl").as(String.class), "%" + sysResourceSearchDto.getSourceUrl() + "");
                    list.add(predicate);
                }

                if (!StringUtils.isBlank(sysResourceSearchDto.getDescript())) {
                    Predicate predicate = criteriaBuilder.like(root.get("descript").as(String.class), "%" + sysResourceSearchDto.getDescript() + "%");
                    list.add(predicate);
                }

                if (sysResourceSearchDto.getIsShow() != null) {
                    Predicate predicate = criteriaBuilder.equal(root.get("isShow").as(String.class), sysResourceSearchDto.getIsShow());
                    list.add(predicate);
                }

                if (sysResourceSearchDto.getParentId() != null) {
                    Predicate predicate = criteriaBuilder.equal(root.get("parentId").as(String.class), sysResourceSearchDto.getParentId());
                    list.add(predicate);
                }

                if (sysResourceSearchDto.getCreateUser() != null) {
                    Predicate predicate = criteriaBuilder.equal(root.get("createUser").as(String.class), sysResourceSearchDto.getCreateUser());
                    list.add(predicate);
                }


                if (sysResourceSearchDto.getStartCreateTime() != null) {
                    Predicate predicate = criteriaBuilder.greaterThanOrEqualTo(root.get("createTime").as(Date.class), sysResourceSearchDto.getStartCreateTime());
                    list.add(predicate);
                }
                if (sysResourceSearchDto.getEndCreateTime() != null) {
                    Predicate predicate = criteriaBuilder.lessThanOrEqualTo(root.get("createTime").as(Date.class), sysResourceSearchDto.getEndCreateTime());
                    list.add(predicate);
                }

                list.add(criteriaBuilder.equal(root.get("deleteStatus").as(Integer.class), 0));

                Predicate[] predicates = new Predicate[list.size()];
                predicates = list.toArray(predicates);
                return criteriaBuilder.and(predicates);
            }
        }, pageRequest);
    }

    @Override
    public SysResource insert(SysResourceModelDto sysResourceDto) {
        SysResource sysResource = new SysResource();
        BeanUtil.copyProperties(sysResourceDto, sysResource, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        sysResource.setId(idWorker.nextId() + "");
        sysResource.setCreateTime(new Date());
        sysResource.setUpdateTime(new Date());
        sysResource.setDeleteStatus(0);
        save(sysResource);
        return sysResource;
    }

    @Override
    public SysResource update(String id, SysResourceModelDto sysResourceDto) {
        SysResource sysResourceDb = find(id);
        if (sysResourceDb == null) {
            throw new GlobleException(CodeMsg.ROLE_NOT_EXIST);
        }

        BeanUtil.copyProperties(sysResourceDto, sysResourceDb, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

        sysResourceDb.setUpdateTime(new Date());
        save(sysResourceDb);
        return sysResourceDb;
    }

    @Override
    public List<SysResource> findResourcesByRoleId(String roleId) {
        return sysRoleResourceMapper.finResourceByRoleId(roleId);
    }

    @Override
    public void dispatchResourceByRoleId(String roleId, String[] resourceIds) {
        if (StringUtils.isBlank(roleId)) {
            throw new GlobleException(CodeMsg.ROLE_ID_NOT_EXIST);
        }
        if (!sysRoleRepository.existsById(roleId)) {
            throw new GlobleException(CodeMsg.ROLE_NOT_EXIST);
        }
        //删除之前的信息
        sysRoleResourceRepository.deleteByRoleId(roleId);

        List<SysRoleResource> sysRoleResourceList = new ArrayList<>();
        for (String resourceId : resourceIds) {
            if (sysResourceRepository.existsById(resourceId)) {
                sysRoleResourceList.add(new SysRoleResource(idWorker.nextId() + "", roleId, resourceId));
            }
        }
        sysRoleResourceMapper.batchInsert(sysRoleResourceList);
    }

    @Override
    public SysResourceTreeDto treeResourcesByParentId(String parentId) {
        SysResourceTreeDto sysResourceTreeDto;
        if ("0".equalsIgnoreCase(parentId)) {
            sysResourceTreeDto = new SysResourceTreeDto();
            sysResourceTreeDto.setId("0");
            sysResourceTreeDto.setName("根节点");
            sysResourceTreeDto.setDescript("根节点不可进行编辑");
        } else {
            sysResourceTreeDto = sysResourceMapper.getResourceById(parentId);
        }
        sysResourceTreeDto.setChildren(getResourcesByParentId(parentId));

        return sysResourceTreeDto;
    }

    @Override
    public void dispatchRoleByResourceId(String resourceId, String[] roleIds) {
        if (StringUtils.isBlank(resourceId)) {
            throw new GlobleException(CodeMsg.RESOURCE_ID_NOT_EXIST);
        }
        if(!sysResourceRepository.existsById(resourceId)){
            throw new GlobleException(CodeMsg.RESOURCE_NOT_EXIST);
        }
        //删除之前的信息
        sysRoleResourceRepository.deleteByResourceId(resourceId);

        List<SysRoleResource> sysRoleResourceList = new ArrayList<>();
        for (String roleId : roleIds) {
            if(sysRoleRepository.existsById(roleId)){
                sysRoleResourceList.add(new SysRoleResource(idWorker.nextId() + "", roleId, resourceId));
            }
        }
        sysRoleResourceMapper.batchInsert(sysRoleResourceList);
    }

    /**
     * 递归查询子元素
     *
     * @param parentId
     * @return
     */
    private List<SysResourceTreeDto> getResourcesByParentId(String parentId) {
        List<SysResourceTreeDto> list = sysResourceMapper.getResourceByParentId(parentId);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setChildren(getResourcesByParentId(list.get(i).getId()));
        }
        return list;
    }
}
