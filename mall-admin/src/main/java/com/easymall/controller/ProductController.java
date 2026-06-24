package com.easymall.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.easymall.entity.DTO.ProductPublishDTO;
import com.easymall.entity.DTO.UpdateStockDTO;
import com.easymall.entity.ProductBase;
import com.easymall.entity.ProductInfoQuery;
import com.easymall.entity.constants.vo.ResponseVO;
import com.easymall.entity.vo.ProductListVO;
import com.easymall.service.ProductService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController extends ABaseController {

    @Resource
    private ProductService productService;


    /**
     * 发布商品（新增或编辑）
     */
    @PostMapping("/publish")
    public ResponseVO publish(@RequestBody ProductPublishDTO publishDTO) {
        try {
            String productId = productService.publishProduct(publishDTO);
            log.info("商品发布成功: {}", productId);
            return getSuccessResponseVO(productId);
        } catch (IllegalArgumentException e) {
            log.warn("发布商品参数错误: {}", e.getMessage());
            return getFailResponseVO(e.getMessage());
        } catch (Exception e) {
            log.error("发布商品失败: {}", e.getMessage(), e);
            return getFailResponseVO("发布商品失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询商品列表
     */
    @GetMapping("/list")
    public ResponseVO list(ProductInfoQuery query){
        try {
            IPage<ProductListVO> page = productService.getPageByParam(query);
            return getSuccessResponseVO(page);
        } catch (Exception e) {
            log.error("查询商品列表失败: {}", e.getMessage(), e);
            return getFailResponseVO("查询商品列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取商品信息根据商品id
     */
    @GetMapping("/getProductById/{productId}")
    public ResponseVO getProductById(@PathVariable String productId) {
        try {
            if (productId == null || productId.trim().isEmpty()) {
                return getFailResponseVO("商品ID不能为空");
            }
            ProductBase product = productService.getProductById(productId);
            log.info("获取商品信息成功: {}", product);
            return getSuccessResponseVO(product);
        } catch (IllegalArgumentException e) {
            log.warn("获取商品信息参数错误: {}", e.getMessage());
            return getFailResponseVO(e.getMessage());
        }
    }

    
    /**
     * 删除商品
     */
    @PostMapping("/delete/{productId}")
    public ResponseVO delete(@PathVariable String productId) {
        try {
            if (productId == null || productId.trim().isEmpty()) {
                return getFailResponseVO("商品ID不能为空");
            }
            productService.deleteProduct(productId);
            log.info("商品删除成功: {}", productId);
            return getSuccessResponseVO(null);
        } catch (IllegalArgumentException e) {
            log.warn("删除商品参数错误: {}", e.getMessage());
            return getFailResponseVO(e.getMessage());
        } catch (Exception e) {
            log.error("删除商品失败: {}", e.getMessage(), e);
            return getFailResponseVO("删除商品失败: " + e.getMessage());
        }
    }
    
    /**
     * 修改商品推荐状态
     */
    @PostMapping("/updateCommendType")
    public ResponseVO updateCommendType(@RequestParam String productId, 
                                        @RequestParam Integer commendType) {
        try {
            if (productId == null || productId.trim().isEmpty()) {
                return getFailResponseVO("商品ID不能为空");
            }
            if (commendType == null || (commendType != 0 && commendType != 1)) {
                return getFailResponseVO("推荐类型无效，只能为0或1");
            }
            productService.updateCommendType(productId, commendType);
            log.info("商品推荐状态修改成功: {}, commendType: {}", productId, commendType);
            return getSuccessResponseVO(null);
        } catch (IllegalArgumentException e) {
            log.warn("修改推荐状态参数错误: {}", e.getMessage());
            return getFailResponseVO(e.getMessage());
        } catch (Exception e) {
            log.error("修改推荐状态失败: {}", e.getMessage(), e);
            return getFailResponseVO("修改推荐状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 修改商品上下架状态
     */
    @PostMapping("/updateStatus")
    public ResponseVO updateStatus(@RequestParam String productId, 
                                   @RequestParam Integer status) {
        try {
            if (productId == null || productId.trim().isEmpty()) {
                return getFailResponseVO("商品ID不能为空");
            }
            if (status == null || (status != 0 && status != 1)) {
                return getFailResponseVO("状态无效，只能为0或1");
            }
            productService.updateStatus(productId, status);
            log.info("商品状态修改成功: {}, status: {}", productId, status);
            return getSuccessResponseVO(null);
        } catch (IllegalArgumentException e) {
            log.warn("修改状态参数错误: {}", e.getMessage());
            return getFailResponseVO(e.getMessage());
        } catch (Exception e) {
            log.error("修改状态失败: {}", e.getMessage(), e);
            return getFailResponseVO("修改状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量修改商品SKU库存
     */
    @PostMapping("/updateSkuStock")
    public ResponseVO updateStock(@RequestBody UpdateStockDTO dto) {
        try {
            productService.updateStock(dto);
            log.info("商品SKU库存批量修改成功: productId: {}, sku数量: {}", 
                    dto.getProductId(), dto.getSkus().size());
            return getSuccessResponseVO(null);
        } catch (IllegalArgumentException e) {
            log.warn("修改库存参数错误: {}", e.getMessage());
            return getFailResponseVO(e.getMessage());
        } catch (Exception e) {
            log.error("修改库存失败: {}", e.getMessage(), e);
            return getFailResponseVO("修改库存失败: " + e.getMessage());
        }
    }

    /**
     * 加载商品SKU库存数据
     */
    @GetMapping("/loadStock/{productId}")
    public ResponseVO loadStock(@PathVariable String productId) {
        try {
            if (productId == null || productId.trim().isEmpty()) {
                return getFailResponseVO("商品ID不能为空");
            }
            return getSuccessResponseVO(productService.loadStock(productId));
        } catch (IllegalArgumentException e) {
            log.warn("加载库存参数错误: {}", e.getMessage());
            return getFailResponseVO(e.getMessage());
        } catch (Exception e) {
            log.error("加载库存失败: {}", e.getMessage(), e);
            return getFailResponseVO("加载库存失败: " + e.getMessage());
        }
    }

    /**
     * 修改商品备注
     */
    @PostMapping("/updateRemark")
    public ResponseVO updateRemark(@RequestParam String productId,
                                   @RequestParam String remark) {
        try {
            if (productId == null) {
                return getFailResponseVO("productId不能为空");
            }
            productService.updateRemark(productId, remark);
            log.info("商品备注修改成功: skuId: {}, remark: {}", productId, remark);
            return getSuccessResponseVO(null);
        } catch (IllegalArgumentException e) {
            log.warn("修改备注参数错误: {}", e.getMessage());
            return getFailResponseVO(e.getMessage());
        } catch (Exception e) {
            log.error("修改备注失败: {}", e.getMessage(), e);
            return getFailResponseVO("修改备注失败: " + e.getMessage());
        }

    }


}
