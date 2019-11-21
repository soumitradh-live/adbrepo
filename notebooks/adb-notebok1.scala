// Databricks notebook source
// MAGIC %sql
// MAGIC 
// MAGIC Create Database IF NOT EXISTS PractiseDB;

// COMMAND ----------

// MAGIC %sql
// MAGIC 
// MAGIC CREATE TABLE IF NOT EXISTS PractiseDB.Customers
// MAGIC USING CSV
// MAGIC OPTIONS
// MAGIC (
// MAGIC   path "mnt/data/customers/*.csv",
// MAGIC   sep ",",
// MAGIC   header "true",
// MAGIC   inferSchema "true"
// MAGIC )

// COMMAND ----------

// MAGIC %sql
// MAGIC 
// MAGIC SELECT COUNT(*) FROM PractiseDB.Customers

// COMMAND ----------

// MAGIC %sql
// MAGIC 
// MAGIC CREATE TABLE IF NOT EXISTS PractiseDB.Products
// MAGIC USING JSON
// MAGIC OPTIONS
// MAGIC (
// MAGIC   path "/mnt/data/products/*.json",
// MAGIC   multiline "true",
// MAGIC   failfast "true"
// MAGIC )

// COMMAND ----------

// MAGIC %sql
// MAGIC SELECT COUNT(*) FROM PractiseDB.Products

// COMMAND ----------

// MAGIC %sql
// MAGIC 
// MAGIC CREATE TABLE IF NOT EXISTS PractiseDB.Orders
// MAGIC USING CSV
// MAGIC OPTIONS
// MAGIC (
// MAGIC   path "/mnt/data/orders/*.csv",
// MAGIC   sep ",",
// MAGIC   header "true",
// MAGIC   inferSchema "true"  
// MAGIC )

// COMMAND ----------

// MAGIC %sql
// MAGIC SELECT COUNT(*) FROM PractiseDB.Orders

// COMMAND ----------

// MAGIC %sql 
// MAGIC USE PractiseDB;
// MAGIC SHOW TABLES

// COMMAND ----------

val getOrderAmount = (units: Int, unitPrice: Int, itemdiscount: Int) => {
  val total = (units * unitPrice)
  val discount = ((total * itemdiscount) / 100).asInstanceOf[Int]
  
  (total - discount).asInstanceOf[Int]
}

val getCustomerType = (credit: Int) => {
  if(credit < 10000) "Silver"
  else if(credit >= 10000 && credit < 25000) "Gold"
  else "Platinum"
}

spark.udf.register("getCustomerType", getCustomerType)
spark.udf.register("getOrderAmount", getOrderAmount)

// COMMAND ----------

// MAGIC %sql
// MAGIC 
// MAGIC SELECT o.orderid AS OrderId, o.orderdate AS OrderDate, c.customername AS CustomerName, p.title AS ProductTitle,
// MAGIC     c.address AS CustomerLocation, getCustomerType(c.credit) AS CustomerType,
// MAGIC     getOrderAmount(o.units, p.unitprice, p.itemdiscount) AS OrderAmount,
// MAGIC     p.unitprice AS UnitPrice, p.itemdiscount AS ItemDiscount,
// MAGIC     o.billingaddress AS BillingAddress, o.remarks AS OrderRemarks
// MAGIC   FROM PractiseDB.Orders o
// MAGIC   INNER JOIN  PractiseDB.Customers c ON c.customerid = o.customer
// MAGIC   INNER JOIN PractiseDB.Products p ON p.productid = o.product
// MAGIC   WHERE o.billingaddress IN ( 'Bangalore', 'Trivandrum', 'Hyderabad', 'Mumbai', 'Chennai', 'New Delhi')
// MAGIC   ORDER BY OrderAmount

// COMMAND ----------

// MAGIC %sql
// MAGIC 
// MAGIC CREATE TABLE IF NOT EXISTS PractiseDB.ProcessedOrders
// MAGIC USING PARQUET
// MAGIC OPTIONS
// MAGIC (
// MAGIC   path "/mnt/data/processed-orders",
// MAGIC   failfast "true"
// MAGIC )
// MAGIC AS
// MAGIC SELECT CAST(o.orderid AS STRING) AS OrderId, o.orderdate AS OrderDate, c.customername AS CustomerName, p.title AS ProductTitle,
// MAGIC     c.address AS CustomerLocation, getCustomerType(c.credit) AS CustomerType,
// MAGIC     getOrderAmount(o.units, p.unitprice, p.itemdiscount) AS OrderAmount,
// MAGIC     p.unitprice AS UnitPrice, p.itemdiscount AS ItemDiscount,
// MAGIC     o.billingaddress AS BillingAddress, o.remarks AS OrderRemarks
// MAGIC   FROM PractiseDB.Orders o
// MAGIC   INNER JOIN  PractiseDB.Customers c ON c.customerid = o.customer
// MAGIC   INNER JOIN PractiseDB.Products p ON p.productid = o.product
// MAGIC   WHERE o.billingaddress IN ( 'Bangalore', 'Trivandrum', 'Hyderabad', 'Mumbai', 'Chennai', 'New Delhi')
// MAGIC   ORDER BY OrderAmount

// COMMAND ----------

// MAGIC %sql
// MAGIC 
// MAGIC CREATE TABLE IF NOT EXISTS PractiseDB.PreProcessedOrders
// MAGIC USING PARQUET
// MAGIC OPTIONS
// MAGIC (
// MAGIC   path "/mnt/data/processed-orders",
// MAGIC   failfast "true"
// MAGIC )

// COMMAND ----------

// MAGIC %sql
// MAGIC 
// MAGIC select * FROM PractiseDB.PreProcessedOrders

// COMMAND ----------

