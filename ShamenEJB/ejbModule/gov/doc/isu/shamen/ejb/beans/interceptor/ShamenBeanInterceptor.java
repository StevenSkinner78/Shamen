// /**
// *
// */
// package gov.doc.isu.shamen.ejb.beans.interceptor;
//
// import gov.doc.isu.shamen.ejb.util.ShamenEJBUtil;
//
// import java.io.Serializable;
//
// import javax.annotation.PostConstruct;
// import javax.interceptor.AroundInvoke;
// import javax.interceptor.InvocationContext;
//
// import org.apache.log4j.Logger;
//
// /**
// * This class is a lifecycle callback interceptor for all gov.doc.isu.shamen.ejb.beans. The callback methods simply print a message when invoked by the container.
// *
// * @author Joseph Burris JCCC
// */
// public class ShamenBeanInterceptor implements Serializable {
// /**
// *
// */
// private static final long serialVersionUID = 1L;
// private Logger logger = Logger.getLogger("gov.doc.isu.shamen.ejb.beans.interceptor.ShamenBeanInterceptor");
//
// /**
// * This default constructor is used to instantiate this class.
// */
// public ShamenBeanInterceptor() {
// super();
// }// end constructor
//
// /**
// * This method is called by the container after construction.
// *
// * @param ctx
// * The current {@link InvocationContext}.
// */
// @PostConstruct
// public void initialize(InvocationContext ctx) {
// try{
// if(logger.isTraceEnabled()){
// logger.debug("The " + ctx.getMethod().getName() + " has been constructed.");
// }
// ctx.proceed();
// }catch(Exception e){
// logger.error("An exception occurred while allowing the invocation context to proceed from post-construct interceptor. Message is: " + String.valueOf(ctx), e);
// }// end try/catch
// }// end initialize
//
// /**
// * This method is called by the container around invoke of methods.
// *
// * @param ctx
// * The current {@link InvocationContext}.
// * @return Object
// * @throws Exception
// * if an exception occurred
// */
// @AroundInvoke
// public Object inteceptorMethod(InvocationContext ctx) throws Exception {
// long start = System.currentTimeMillis();// this is for logging purposes only!!!! can remove later.
// try{
// if(logger.isTraceEnabled()){
// logger.debug("Executing operation => " + ctx.getMethod().getName());
// }
// return ctx.proceed();
// }finally{
// logger.debug("Processing for method: " + ctx.getMethod().getName() + " took: " + ShamenEJBUtil.getTimeTookInSecMinHours(start));
//
// }// end try/catch
// }// end initialize
//
// }// end class
