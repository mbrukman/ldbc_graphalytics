package nl.tudelft.graphalytics.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Results of the execution of the Graphalytics benchmark suite on a single platform. Includes configuration details
 * of both the system and platform, in addition to the individual benchmark results.
 *
 * @author Tim Hegeman
 */
public class BenchmarkSuiteResult implements Serializable {

	private final BenchmarkSuite benchmarkSuite;
	private final Collection<BenchmarkResult> benchmarkResults;

	private final PlatformConfiguration platformConfiguration;
	private final SystemConfiguration systemConfiguration;

	/**
	 * @param benchmarkSuite        the benchmark suite for which this result was obtained
	 * @param benchmarkResults      the collection of individual benchmark results for each benchmark in the suite
	 * @param platformConfiguration the platform-specific configuration options used during execution of the benchmark suite
	 * @param systemConfiguration   the configuration of the system used to run the benchmark suite
	 */
	private BenchmarkSuiteResult(BenchmarkSuite benchmarkSuite, Collection<BenchmarkResult> benchmarkResults,
	                             PlatformConfiguration platformConfiguration, SystemConfiguration systemConfiguration) {
		this.benchmarkSuite = benchmarkSuite;
		this.benchmarkResults = benchmarkResults;
		this.platformConfiguration = platformConfiguration;
		this.systemConfiguration = systemConfiguration;
	}

	/**
	 * @return the benchmark suite for which this result was obtained
	 */
	public BenchmarkSuite getBenchmarkSuite() {
		return benchmarkSuite;
	}

	/**
	 * @return the collection of individual benchmark results for each benchmark in the suite
	 */
	public Collection<BenchmarkResult> getBenchmarkResults() {
		return benchmarkResults;
	}

	/**
	 * @return the platform-specific configuration options used during execution of the benchmark suite
	 */
	public PlatformConfiguration getPlatformConfiguration() {
		return platformConfiguration;
	}

	/**
	 * @return the configuration of the system used to run the benchmark suite
	 */
	public SystemConfiguration getSystemConfiguration() {
		return systemConfiguration;
	}

	/**
	 * Factory for creating a new BenchmarkSuiteResult. Guarantees that each benchmark in the suite has
	 * exactly one result associated with it.
	 */
	public static class BenchmarkSuiteResultBuilder {
		private final Map<Benchmark, BenchmarkResult> benchmarkResultMap = new HashMap<>();
		private BenchmarkSuite benchmarkSuite;

		/**
		 * Constructs a new BenchmarkSuiteResultBuilder that can be used to create a new BenchmarkSuiteResult.
		 *
		 * @param benchmarkSuite the benchmark suite for which to collect results
		 * @throws IllegalArgumentException iff benchmarkSuite is null
		 */
		public BenchmarkSuiteResultBuilder(BenchmarkSuite benchmarkSuite) {
			if (benchmarkSuite == null)
				throw new IllegalArgumentException("Parameter \"benchmarkSuite\" must not be null.");

			this.benchmarkSuite = benchmarkSuite;
		}

		/**
		 * Adds a BenchmarkResult to the list of results for this BenchmarkSuite. Overrides any previous result
		 * for the same benchmark.
		 *
		 * @param benchmarkResult a benchmark result to add to the results of the benchmark suite
		 * @return a reference to this
		 * @throws IllegalArgumentException if benchmarkResult is null or if benchmarkResult corresponds to a benchmark
		 *                                  that is not part of the suite
		 */
		public BenchmarkSuiteResultBuilder withBenchmarkResult(BenchmarkResult benchmarkResult) {
			if (benchmarkResult == null)
				throw new IllegalArgumentException("Parameter \"benchmarkResult\" must not be null.");
			if (!benchmarkSuite.getBenchmarks().contains(benchmarkResult.getBenchmark()))
				throw new IllegalArgumentException("\"benchmarkResult\" must refer to a benchmark that is part of the suite.");

			benchmarkResultMap.put(benchmarkResult.getBenchmark(), benchmarkResult);
			return this;
		}

		/**
		 * Builds the BenchmarkSuiteResult object with the given configuration details.
		 *
		 * @param systemConfiguration   the configuration of the system used to run the benchmark suite
		 * @param platformConfiguration the platform-specific configuration options used during execution of the
		 *                              benchmark suite
		 * @return a new BenchmarkSuiteResult
		 * @throws IllegalArgumentException iff systemConfiguration is null or platformConfiguration is null
		 */
		public BenchmarkSuiteResult buildFromConfiguration(SystemConfiguration systemConfiguration,
		                                                   PlatformConfiguration platformConfiguration) {
			if (systemConfiguration == null)
				throw new IllegalArgumentException("Parameter \"systemConfiguration\" must not be null.");
			if (platformConfiguration == null)
				throw new IllegalArgumentException("Parameter \"platformConfiguration\" must not be null.");

			// Add benchmark results ("not run") for any benchmark that does not have a corresponding result
			for (Benchmark benchmark : benchmarkSuite.getBenchmarks()) {
				if (!benchmarkResultMap.containsKey(benchmark))
					benchmarkResultMap.put(benchmark, BenchmarkResult.forBenchmarkNotRun(benchmark));
			}

			return new BenchmarkSuiteResult(benchmarkSuite, benchmarkResultMap.values(), platformConfiguration,
					systemConfiguration);
		}

	}

}
