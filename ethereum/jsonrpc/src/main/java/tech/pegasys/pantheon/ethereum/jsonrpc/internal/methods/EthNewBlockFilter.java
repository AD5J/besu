package tech.pegasys.pantheon.ethereum.jsonrpc.internal.methods;

import tech.pegasys.pantheon.ethereum.jsonrpc.internal.JsonRpcRequest;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.filter.FilterManager;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcResponse;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.response.JsonRpcSuccessResponse;

public class EthNewBlockFilter implements JsonRpcMethod {

  private final FilterManager filterManager;

  public EthNewBlockFilter(final FilterManager filterManager) {
    this.filterManager = filterManager;
  }

  @Override
  public String getName() {
    return "eth_newBlockFilter";
  }

  @Override
  public JsonRpcResponse response(final JsonRpcRequest req) {
    return new JsonRpcSuccessResponse(req.getId(), filterManager.installBlockFilter());
  }
}
