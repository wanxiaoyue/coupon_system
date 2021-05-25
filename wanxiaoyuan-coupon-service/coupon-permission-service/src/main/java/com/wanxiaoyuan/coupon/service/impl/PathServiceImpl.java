package com.wanxiaoyuan.coupon.service.impl;

import com.wanxiaoyuan.coupon.dao.PathRepository;
import com.wanxiaoyuan.coupon.entity.Path;
import com.wanxiaoyuan.coupon.service.IPathService;
import com.wanxiaoyuan.coupon.vo.CreatePathRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1> 路径相关的服务功能实现 </h1>
 * Created by WanYue
 */

@Slf4j
@Service
public class PathServiceImpl implements IPathService {

    /** pathRepository*/
    private final PathRepository pathRepository;

    @Autowired
    public PathServiceImpl(PathRepository pathRepository) {
        this.pathRepository = pathRepository;
    }


    /**
     * <h2>添加新的 path 到数据表中</h2>
     * @param request {@link CreatePathRequest}
     * @return Path 数据记录的主键
     */
    @Override
    public List<Integer> createPath(CreatePathRequest request) {

        List<CreatePathRequest.PathInfo> pathInfos = request.getPathInfos();
        List<CreatePathRequest.PathInfo> validRequests =
                new ArrayList<>(request.getPathInfos().size());

        List<Path> currentPaths = pathRepository.findAllByServiceName(  //通过request 里面的serviceName获取数据库info中已经注册的信息
                pathInfos.get(0).getServiceName()
        );

        //当前微服务的路径信息
        if(!CollectionUtils.isEmpty(currentPaths)){
            for (CreatePathRequest.PathInfo pathInfo : pathInfos) {
                boolean isValid = true;

                for (Path currentPath : currentPaths) {
                    //两个校验，这两个校验，isvalid = false ,为什么要唯一呢？确定唯一的微服务接口，如果相等说明数据已有
                    if(currentPath.getPathPattern()
                            .equals(pathInfo.getPathPattern()) &&
                    currentPath.getHttpMethod().equals(pathInfo.getHttpMethod())){
                        isValid = false;
                        break;
                    }
                }
                if(isValid){
                    validRequests.add(pathInfo) ;
                }
            }
        }else {
            validRequests = pathInfos;
        }

                List<Path> paths = new ArrayList<>(validRequests.size());

                validRequests.forEach(p -> paths.add(new Path(
                        p.getPathPattern(),
                        p.getHttpMethod(),
                        p.getPathName(),
                        p.getServiceName(),
                        p.getOpMode()
                )));


        return pathRepository.saveAll(paths)
                .stream().map(Path::getId).collect(Collectors.toList());
    }
}
