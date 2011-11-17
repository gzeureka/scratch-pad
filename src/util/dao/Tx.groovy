package util.dao

import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.transaction.support.TransactionCallback

/**
 * 用于多个Manager参与同一个事务或者对同一个Manager调用多次
 * 例如:
 * Tx.doIt {
 *    someManager.doSomething()
 *    anotherManger.doAnotherThing()
 * } 
 * 
 * Tx.doIt {
 *    someManager.doSomething()
 *    someManager.doAnotherThing()
 * }
 * 
 * Tx.doIt {
 *    list.each{someManager.doSomething(it)}
 * }
 */
class Tx {
	static PlatformTransactionManager transactionManager
	
	def static doIt(Closure closure) {
		def tt = new TransactionTemplate(transactionManager)
		def ret
		tt.execute({
			ret = closure.call()
		} as TransactionCallback)
		
		return ret
	}
	
	/**
	 * 在一个新建的事务中执行closure
	 * @param closure
	 * @return
	 */
	def static doInNewTx(String isolationLevel='ISOLATION_DEFAULT', Closure closure) {
		def tt = new TransactionTemplate(transactionManager)
		tt.setPropagationBehaviorName('PROPAGATION_REQUIRES_NEW')
		tt.setIsolationLevelName(isolationLevel)
		
		def ret
		tt.execute({
			ret = closure.call()
		} as TransactionCallback)
		
		return ret
	}
}
