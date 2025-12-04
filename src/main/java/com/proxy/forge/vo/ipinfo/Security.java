/**
 * Copyright 2025 lzltool.com
 */

package com.proxy.forge.vo.ipinfo;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * Auto-generated: 2025-12-02 00:09:59
 *
 * @author lzltool.com
 * @website https://www.lzltool.com/JsonToJava
 */
@Data
public class Security {

	@JSONField(name ="is_abuser")
	private boolean isAbuser;
	@JSONField(name ="is_attacker")
	private boolean isAttacker;
	@JSONField(name ="is_bogon")
	private boolean isBogon;
	@JSONField(name ="is_cloud_provider")
	private boolean isCloudProvider;
	@JSONField(name ="is_proxy")
	private boolean isProxy;
	@JSONField(name ="is_relay")
	private boolean isRelay;
	@JSONField(name ="is_tor")
	private boolean isTor;
	@JSONField(name ="is_tor_exit")
	private boolean isTorExit;
	@JSONField(name ="is_vpn")
	private boolean isVpn;
	@JSONField(name ="is_anonymous")
	private boolean isAnonymous;
	@JSONField(name ="is_threat")
	private boolean isThreat;

}