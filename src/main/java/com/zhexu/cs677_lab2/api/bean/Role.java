package com.zhexu.cs677_lab2.api.bean;


import com.zhexu.cs677_lab2.api.bean.basic.Address;
import com.zhexu.cs677_lab2.api.bean.basic.PeerBase;
import com.zhexu.cs677_lab2.api.bean.basic.Product;
import com.zhexu.cs677_lab2.api.bean.basic.factories.SingletonFactory;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

import static com.zhexu.cs677_lab2.constants.Consts.*;


/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/25/22
 **/
@Log4j2
public class Role extends PeerBase implements Serializable {
    private Boolean isBuyer = Boolean.FALSE;
    private Boolean isSeller = Boolean.FALSE;
    private Map<Integer, Product> productMap;

    private Map<Product, Integer> stock;

    public Role(UUID id, Map<UUID, Address> neighbourPeerList, Address address, Map<Integer, Product> productMap, Map<Product, Integer> stock) {
        super(id, neighbourPeerList, address);
        this.productMap = productMap;
        this.stock = stock;
    }


    public Map<Product, Integer> getStock() {
        return stock;
    }

    public Integer getProductMapSize(){
        return this.productMap.size();
    }

    public Integer getStockByProductId(Integer id) {
        Product targetProduct = this.productMap.get(id);

        if (this.stock.isEmpty() || null == this.stock.get(targetProduct)){
            return ZERO;
        }

        if (this.stock.get(targetProduct) < SingletonFactory.getMaxStock()){
            Random random = new Random();
            Integer reStockNumber = random.nextInt(SingletonFactory.getMaxStock());
            this.stock.put(targetProduct, this.stock.get(targetProduct) + reStockNumber);
            log.info("Restock: " + targetProduct.getProductName() + "with number of: " + reStockNumber);
        }

        return this.stock.get(targetProduct);
    }

    public Integer consumeStockByProductId(Integer id, Integer number){
        Product targetProduct = this.productMap.get(id);
        if (!this.stock.isEmpty()
                && null != this.stock.get(targetProduct)
                && this.stock.get(targetProduct) - number >= 0){
            this.stock.put(
                    targetProduct, this.stock.get(targetProduct) - number
            );
            return this.stock.get(targetProduct);
        }
        return -1;
    }

    public void reImportWhenSold() {
        Random ra = new Random();
        List<Product> productList = SingletonFactory.getProductList();
        Integer maxStock = SingletonFactory.getMaxStock();
        Map<Product, Integer> newStock = new HashMap<>();
        newStock.put(
                productList.get(ra.nextInt(productList.size())), ra.nextInt(maxStock)
        );
        Logger logger = Logger.getLogger(LOGGER_TRADE);
        logger.info("Stock updated:" + ENTER +
                this.getStock() + ENTER);
    }

    public void setStock(Map<Product, Integer> stock) {
        this.stock = stock;
    }

    public Integer quaryStock(Product product) {
        return this.stock.get(product);
    }

    public Integer addStock(Product product, Integer number) {
        this.stock.put(product, this.stock.get(product) + number);
        return this.stock.get(product);
    }

    public Integer cosumeStock(Product product, Integer number) {
        if (!this.stock.containsKey(product)) {
            return -1;
        }
        if (this.stock.get(product) >= number) {
            this.stock.put(product, this.stock.get(product) - number);
            if (0 >= this.stock.get(product)) {
                reImportWhenSold();
                return 0;
            }
            return this.stock.get(product);
        }
        return -2;
    }

    public Boolean ifProductInStore(Product product) {
        return this.stock.containsKey(product);
    }

    public Boolean isBuyer() {
        return this.isBuyer;
    }

    public Boolean isSeller() {
        return this.isSeller;
    }

    public void becomeBuyer() {
        this.isBuyer = Boolean.TRUE;
    }

    public void becomeSeller() {
        this.isSeller = Boolean.TRUE;
    }

    public void stopBuying() {
        this.isBuyer = Boolean.FALSE;
    }

    public void stopSelling() {
        this.isSeller = Boolean.FALSE;
    }

    public Map<Integer, Product> getProductMap() {
        return productMap;
    }

    public void setProductMap(Map<Integer, Product> productMap) {
        this.productMap = productMap;
    }

    @Override
    public String toString() {
        return "Role{" +
                "shopList=" + productMap +
                ", stock=" + stock +
                '}';
    }
}
