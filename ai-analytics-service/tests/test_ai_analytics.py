import unittest
import json

class TestAiAnalytics(unittest.TestCase):

    def test_prompt_formatting(self):
        event_data = {
            "productionOrderId": "12345",
            "productSku": "BED-KING",
            "targetQuantity": 2,
            "timestamp": 1785653237.95
        }
        prompt = f"Analyze the production order for SKU: {event_data['productSku']} with Quantity: {event_data['targetQuantity']}."
        self.assertIn("BED-KING", prompt)
        self.assertIn("Quantity: 2", prompt)

    def test_json_payload_parsing(self):
        raw_kafka_payload = '{"eventId": "abc-123", "referenceCode": "ORD-001", "totalAmount": 87500.50}'
        parsed = json.loads(raw_kafka_payload)
        self.assertEqual(parsed["referenceCode"], "ORD-001")
        self.assertEqual(parsed["totalAmount"], 87500.50)

if __name__ == '__main__':
    unittest.main()
