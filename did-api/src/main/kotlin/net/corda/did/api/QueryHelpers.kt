package net.corda.did.api
/**
 * Persistent code
 *
 */

import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.messaging.vaultQueryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.did.DidDocument
import net.corda.did.state.DidState
import net.corda.did.state.DidStatus

class QueryUtil(private val proxy: CordaRPCOps) {


    fun getDIDDocumentByLinearId(linearId: String): String {
        val criteria= QueryCriteria.LinearStateQueryCriteria(linearId = listOf(UniqueIdentifier.fromString(linearId)))
        val results = proxy.vaultQueryBy<DidState>(criteria).states
        try {
            val responseState = results.singleOrNull()!!.state
            if(responseState.data.status == DidStatus.DELETED){
                throw  DIDDeletedException( APIMessage.DID_DELETED.message)
            }
            return responseState.data.envelope.rawDocument

        }
        catch(e : NullPointerException){
             return ""
        }


    }
    fun getCompleteDIDDocumentByLinearId( linearId: String ): DidDocument {
        val criteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(UniqueIdentifier.fromString(linearId)))
        val results = proxy.vaultQueryBy<DidState>(criteria).states
        val responseState = results.singleOrNull()!!.state
        if(responseState.data.status == DidStatus.DELETED){
                throw  DIDDeletedException(  APIMessage.DID_DELETED.message )
        }
        return responseState.data.envelope.document

    }





}

class DIDDeletedException(message:String):Exception(message)