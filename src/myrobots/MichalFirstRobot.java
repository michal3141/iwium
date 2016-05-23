package myrobots;

import java.io.FileInputStream;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.rules.InvalidRuleSessionException;
import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleServiceProviderManager;
import javax.rules.StatelessRuleSession;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;

import robocode.BattleEndedEvent;

/**
 * Copyright (c) 2001-2016 Michal Mrowczyk and robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 */

import robocode.HitByBulletEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;

/**
 * MichalFirstRobot - a sample robot by Michal Mrowczyk using javax.riles.
 */
public class MichalFirstRobot extends Robot {

	StatelessRuleSession ruleSession = null;
	List<Object> props = null;
	boolean configExecuted = false;
	String robotState;
	Turn turnAngle = new Turn(0);
	
	public void setRobotState(String newState) {
		System.out.println("Entering " + newState + " state");
		this.robotState = newState;
	}
	
	public String getRobotState() {
		return this.robotState;
	}
	
	
	public void configureRules() {

		try {
			// Load the rule service provider of the reference implementation.
			// Loading this class will automatically register this provider with
			// the provider manager.
			Class.forName("org.jruleengine.RuleServiceProviderImpl");

			// get the rule service provider from the provider manager
			RuleServiceProvider serviceProvider = RuleServiceProviderManager.getRuleServiceProvider("org.jruleengine");

			// get the RuleAdministrator
			RuleAdministrator ruleAdministrator = serviceProvider.getRuleAdministrator();
			System.out.println("\nAdministration API\n");
			System.out.println("Acquired RuleAdministrator: " + ruleAdministrator);

			// get an input stream to a test XML ruleset
			InputStream inStream = new FileInputStream("C:\\Users\\sg0219906\\Desktop\\STUDIA\\iwium\\robocode\\Robocode\\roborules.xml");
			System.out.println("Acquired InputStream to roborules.xml: " + inStream);

			// parse the ruleset from the XML document
			RuleExecutionSet res1 = ruleAdministrator.getLocalRuleExecutionSetProvider(null)
					.createRuleExecutionSet(inStream, null);
			inStream.close();
			System.out.println("Loaded RuleExecutionSet: " + res1);

			// register the RuleExecutionSet
			String uri = res1.getName();
			ruleAdministrator.registerRuleExecutionSet(uri, res1, null);
			System.out.println("Bound RuleExecutionSet to URI: " + uri);

			// Get a RuleRuntime and invoke the rule engine.
			System.out.println("\nRuntime API\n");

			RuleRuntime ruleRuntime = serviceProvider.getRuleRuntime();
			System.out.println("Acquired RuleRuntime: " + ruleRuntime);

			// create a StatefulRuleSession
			ruleSession = (StatelessRuleSession) ruleRuntime.createRuleSession(uri, new HashMap<Object, Object>(),
					RuleRuntime.STATELESS_SESSION_TYPE);
			
			props = new ArrayList<Object>();
			props.add(this);
			props.add(turnAngle);
//			ruleSession.addObject(this);
//			ruleSession.addObject(turnAngle);

		} catch (NoClassDefFoundError e) {
			if (e.getMessage().indexOf("Exception") != -1) {
				System.err.println("Error: The Rule Engine Implementation could not be found.");
			} else {
				System.err.println("Error: " + e.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * MichalFirstRobot's run method - Seesaw
	 */
	public void run() {
		if (!configExecuted) {
			System.out.println("Executing configure rules ...");
			configureRules();
			configExecuted = false;
		}
		
		setRobotState("run");
		while (true) {
			try {
				ruleSession.executeRules(props);
			} catch (InvalidRuleSessionException | RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Fire when we see a robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		setRobotState("onScannedRobot");
		try {
			ruleSession.executeRules(props);
		} catch (InvalidRuleSessionException | RemoteException e1) {
			e1.printStackTrace();
		}
		setRobotState("run");
	}

	/**
	 * We were hit! Turn perpendicular to the bullet, so our seesaw might avoid
	 * a future shot.
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		setRobotState("onHitByBullet");
		try {
			turnAngle.setAngle(90 - e.getBearing());
			ruleSession.executeRules(props);
		} catch (InvalidRuleSessionException | RemoteException e1) {
			e1.printStackTrace();
		}	
		// turnLeft(90 - e.getBearing());
		setRobotState("run");
		
	}
	
	public void onBattleEnded(BattleEndedEvent e) {
		try {
			ruleSession.release();
			System.out.println("ruleSession released.");
		} catch (InvalidRuleSessionException | RemoteException e1) {
			e1.printStackTrace();
		}
	}
}
