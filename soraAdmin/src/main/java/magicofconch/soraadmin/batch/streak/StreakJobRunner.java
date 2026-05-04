package magicofconch.soraadmin.batch.streak;

// TODO: 성능 테스트 중에는 비활성화. GET /test/streak/batch로 수동 트리거
// @Component
// @RequiredArgsConstructor
// public class StreakJobRunner implements ApplicationRunner {
// 	private final JobLauncher jobLauncher;
// 	private final Job streakAlertJob;
//
// 	@Override
// 	public void run(ApplicationArguments args) throws Exception {
// 		JobParameters params = new JobParametersBuilder()
// 			.addLong("run.id", System.currentTimeMillis())
// 			.toJobParameters();
//
// 		jobLauncher.run(streakAlertJob, params);
// 	}
// }
