package com.aiarchitect.service.ai;

/**
 * Strategy interface for AI-powered architecture generation.
 *
 * WHY THE STRATEGY PATTERN:
 * This is one of the most important design patterns in our project.
 *
 * Problem: We're using Gemini today, but what if we want to switch to
 *          OpenAI, Claude, or a self-hosted model tomorrow?
 *
 * Solution: We define an INTERFACE that describes WHAT we need ("generate
 *           architecture from an idea") without specifying HOW to do it.
 *           Each AI provider gets its own implementation class.
 *
 * Benefits:
 * 1. SWAPPABLE: Change AI providers by creating a new implementation class.
 *    No other code in the entire project needs to change.
 * 2. TESTABLE: In unit tests, we can create a mock implementation that
 *    returns fake data instantly (no real API calls, no API costs).
 * 3. OPEN/CLOSED PRINCIPLE: The system is open for extension (add new
 *    providers) but closed for modification (existing code stays untouched).
 *
 * Usage: The ProjectService depends on this INTERFACE, not on
 *        GeminiArchitectureGenerator directly. Spring injects the
 *        active implementation automatically.
 */
public interface ArchitectureGeneratorStrategy {

    /**
     * Takes a plain-English project idea and returns a structured
     * architecture report as a JSON string.
     *
     * @param projectIdea The user's project description (e.g., "Build an E-commerce Backend")
     * @return JSON string containing all architecture report sections
     * @throws com.aiarchitect.exception.AiServiceException if the AI API call fails
     */
    String generateArchitecture(String projectIdea);
}
